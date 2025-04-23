package com.team15gijo.chat.application.service.impl.v1;

import static com.team15gijo.chat.domain.exception.ChatDomainExceptionCode.CHAT_ROOM_INDIVIDUAL_NUMBER_LIMIT;
import static com.team15gijo.chat.domain.exception.ChatDomainExceptionCode.CHAT_ROOM_NOT_FOUND;
import static com.team15gijo.chat.domain.exception.ChatDomainExceptionCode.CHAT_ROOM_USER_ID_NOT_FOUND;
import static com.team15gijo.chat.domain.exception.ChatDomainExceptionCode.MESSAGE_ID_NOT_FOUND;
import static com.team15gijo.chat.domain.exception.ChatDomainExceptionCode.USER_NICK_NAME_NOT_EXIST;

import com.team15gijo.chat.application.dto.v1.ChatRoomResponseDto;
import com.team15gijo.chat.domain.model.v1.ChatMessageDocument;
import com.team15gijo.chat.domain.model.v1.ChatMessageType;
import com.team15gijo.chat.domain.model.v1.ChatRoom;
import com.team15gijo.chat.domain.model.v1.ChatRoomParticipant;
import com.team15gijo.chat.domain.model.v1.ChatRoomType;
import com.team15gijo.chat.domain.model.v1.ConnectionType;
import com.team15gijo.chat.domain.repository.v1.ChatMessageRepository;
import com.team15gijo.chat.domain.repository.v1.ChatRoomParticipantRepository;
import com.team15gijo.chat.domain.repository.v1.ChatRoomRepository;
import com.team15gijo.chat.infrastructure.client.v1.FeignClientService;
import com.team15gijo.chat.infrastructure.kafka.dto.ChatNotificationEventDto;
import com.team15gijo.chat.infrastructure.kafka.service.NotificationKafkaProducerService;
import com.team15gijo.chat.presentation.dto.v1.ChatMessageRequestDto;
import com.team15gijo.chat.application.dto.v1.ChatMessageResponseDto;
import com.team15gijo.chat.presentation.dto.v1.ChatRoomRequestDto;
import com.team15gijo.common.exception.CustomException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomParticipantRepository chatRoomParticipantRepository;

    private final FeignClientService feignClientService;
    private final NotificationKafkaProducerService notificationKafkaProducerService;

    private final RedisTemplate<String, Object> redisTemplate;

    private final SimpMessagingTemplate messagingTemplate;
    private final MongoTemplate mongoTemplate;

    /**
     * 1차 MVP 1:1 채팅만 구현
     * 채팅방 생성(chatRoomType, receiver)에 따른 채팅방 참여자 생성
     * UserFeignClient 사용자 유효성 검사
     */
    @Transactional
    public ChatRoomResponseDto createChatRoom(
        ChatRoomRequestDto requestDto,
        Long userId
    ) {
        log.info("userId = {}", userId);

        // TODO: user에서 userId가 존재하는지 검증
        // TODO: 본인 userId인 경우, 채팅방 생성 제한

        /* 채팅방 생성 시, 상대방 계정 조회(nickname 으로 존재유무 판단)
         * 상대방과 본인 userId를 모두 추출하여 채팅방 참여자 생성
         */
        String receiverNickname = requestDto.getReceiverNickname();
        log.info("receiverNickname = {}", receiverNickname);

        Long getUserIdByNickname = feignClientService.fetchUserIdByNickname(receiverNickname);

        if(getUserIdByNickname == null) {
            throw new CustomException(USER_NICK_NAME_NOT_EXIST);
        }

        // 채팅방 생성하는 당사자의 참여자 생성 및 저장
        ChatRoomParticipant addParticipants = ChatRoomParticipant.builder()
            .userId(userId)
            .activation(Boolean.TRUE)
            .build();
        chatRoomParticipantRepository.save(addParticipants);

        Set<ChatRoomParticipant> participantsSet = new HashSet<>();
        participantsSet.add(addParticipants);

        // 초대받은 참여자의 채팅방 참여자 생성 및 저장
        ChatRoomParticipant invitedParticipant = ChatRoomParticipant.builder()
            .userId(getUserIdByNickname)
            .activation(Boolean.TRUE)
            .build();
        chatRoomParticipantRepository.save(invitedParticipant);
        participantsSet.add(invitedParticipant);

        // 채팅방 생성 및 저장
        ChatRoom chatRoom = ChatRoom.builder()
            .chatRoomType(requestDto.getChatRoomType())
            .chatRoomParticipants(participantsSet)
            .build();
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        // 채팅방 타입이 INDIVIDUAL 인 경우, 참여자 수 제한
        if(savedChatRoom.getChatRoomType() == ChatRoomType.INDIVIDUAL
            && savedChatRoom.getChatRoomParticipants().size() > 2) {
            throw new CustomException(CHAT_ROOM_INDIVIDUAL_NUMBER_LIMIT);
        }
        return savedChatRoom.toResponse();
    }

    /**
     * 채팅방 단일 조회
     */
    @Transactional(readOnly = true)
    public ChatRoomResponseDto getChatRoom(UUID chatRoomId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).
            orElseThrow(() -> new CustomException(CHAT_ROOM_NOT_FOUND));

        if(chatRoom.getChatRoomParticipants().stream()
            .anyMatch(participant -> participant.getUserId().equals(userId))) {
            return chatRoom.toResponse();
        } else {
            throw new CustomException(CHAT_ROOM_USER_ID_NOT_FOUND);
        }
    }

    /**
     * 채팅방 전체 조회
     */
    @Transactional(readOnly = true)
    public Page<ChatRoom> getChatRooms(Pageable pageable, Long userId) {
        log.info("userId = {}", userId);
        List<ChatRoom> chatRoomList = chatRoomRepository.findAll();

        List<ChatRoom> filteredChatRooms = chatRoomList.stream()
            .filter(chatRoom -> chatRoom.getChatRoomParticipants().stream()
                .anyMatch(participant -> participant.getUserId().equals(userId)))
            .toList();

        if(filteredChatRooms.isEmpty()) {
            throw new CustomException(CHAT_ROOM_NOT_FOUND);
        }

        // 페이지네이션 적용
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filteredChatRooms.size());
        List<ChatRoom> pageContent = filteredChatRooms.subList(start, end);

        return new PageImpl<>(pageContent, pageable, pageable.getPageSize());
    }

    /**
     * 채팅방 퇴장(비활성화) -> 삭제(Batch 또는 비동기 처리)
     * -> 1:1 채팅방에서 1명의 사용자 퇴장(채팅방 참여자 비활성화로 변경)
     * -> 채팅방의 모든 참여자 퇴장 시, 채팅방/채팅방 참여자/채팅 메시지 소프트 삭제 처리
     * TODO: 마지막 참여자 activation false 변경 안됨
     */
    @Transactional
    public boolean exitChatRoom(UUID chatRoomId, Long userId) {
        log.info("[exitChatRoom] chatRoomId = {}", chatRoomId);
        log.info("[exitChatRoom] userId = {}", userId);

        ChatRoom chatRoom = chatRoomRepository.findByChatRoomIdAndDeletedAtNull(chatRoomId)
            .orElseThrow(() -> new CustomException(CHAT_ROOM_NOT_FOUND));

        Set<ChatRoomParticipant> chatRoomParticipants = chatRoom.getChatRoomParticipants();
        ChatRoomParticipant targetParticipant = null;
        log.info("[존재하는 참여자 수] chatRoomParticipants.size() = {}", chatRoomParticipants.size());

        // 퇴장 요청 참여자 찾기
        for(ChatRoomParticipant participant : chatRoomParticipants) {
            if(participant.getUserId().equals(userId)) {
                log.info("[exitChatRoom] userId = {}", participant.getUserId());
                log.info("[exitChatRoom] getActivation() 전 = {}", participant.getActivation());
                participant.nonActivate();
                log.info("[exitChatRoom] getActivation() 후 = {}", participant.getActivation());

                targetParticipant = participant;
                break;
            }
        }

        // 비활성화된 참여자 있다면 저장
        if(targetParticipant != null) {
            log.info("[퇴장 원하는 사용자 비활성화 업데이트] chatRoomParticipantRepository.save(participant)");
            chatRoomParticipantRepository.save(targetParticipant);
        }

        // 모든 참여자가 비활성 상태인지 확인 -> 모두 비활성화면 return TRUE
        boolean allParticipantsNonactive = chatRoomParticipants.stream()
            .allMatch(participant -> !participant.getActivation());
        log.info("[exitChatRoom] allParticipantsNonactive = {}", allParticipantsNonactive);

        // 모든 채팅방 참여자가 비활성화 상태면 채팅방 및 참여자 소프트삭제 처리
        if(allParticipantsNonactive) {
            log.info("[exitChatRoom] 모든 채팅방 참여자 비활성화 상태");

            // 비활성화된 참여자 ID의 List
            List<UUID> participantId = new ArrayList<>();
            for(ChatRoomParticipant participant : chatRoomParticipants) {
                log.info("[채팅방 참여자 소프트 삭제 처리] 전, participant.getUserId() = {}", participant.getUserId());
                chatRoomParticipantRepository.deleteByUserId(participant.getUserId());
                participantId.add(participant.getId());
                log.info("[채팅방 참여자 소프트 삭제 처리] 후");
            }

            // chatRoomParticipant 저장 이후, chatRoom 조회하여 삭제된 chatRoomParticipant 삭제 처리되지 않도록 chatRoom 조회 추가(불필요한 query 조회)
            // TODO: 해당 PATCH 로직에서 채팅방 참여자 비활성화만 진행하고, 비동기 또는 배치로 채팅방 삭제 구현 필요!
            ChatRoom updatedChatRoom = chatRoomRepository.findByChatRoomIdAndDeletedAtNull(chatRoomId).orElse(null);
            log.info("updatedChatRoom : {}", updatedChatRoom);
            log.info("[비활성화된 참여자 수] participantId.size()  = {}", participantId.size());
            if(participantId.size() >= 2) {
                log.info("[채팅방 삭제] 전");
                chatRoomRepository.delete(chatRoom);
                log.info("[채팅방 삭제] 후");
            }

            // 채팅방 모든 참여자 userId 메시지 소프트 삭제 메소드
            deleteChatMessageForChatRoomId(chatRoomId, userId);

            return true;
        } else {
            return false;
        }
    }

    /**
     * 수신자 닉네임 검증 및 웹소켓 연결 시, 발송자 닉네임 전달
     */
    public Map<String, Object> validateNickname(String receiverNickname) {
        Long receiverId = feignClientService.fetchUserIdByNickname(receiverNickname);
        log.info("receiverId = {}", receiverId);

        Map<String, Object> response = new HashMap<>();
        // 수신자 닉네임 검증 완료
        if(receiverId != null) {
            log.info("[validateNickname] receiverId = {}, receiverNickname = {}", receiverId, receiverNickname);
            response.put("receiverId", receiverId);
            response.put("receiverNickname", receiverNickname);
        }
        return response;
    }

    /**
     * 소켓 연결 시 사용되는
     * 채팅방 ID 유효성 검증
     */
    @Transactional(readOnly = true)
    public Map<String, Boolean> validateChatRoomId(UUID chatRoomId) {
        Boolean valid = chatRoomRepository.existsById(chatRoomId);
        log.info("valid {}", valid);

        Map<String, Boolean> response = new HashMap<>();
        response.put("valid", valid);

        return response;
    }

    /**
     * 소켓 연결 시 사용되는 엔드포인트
     * 채팅방 ID에 해당하는 senderId(userId) 유효성 검증
     */
    @Transactional(readOnly = true)
    public Map<String, Boolean> validateSenderId(
        UUID chatRoomId,
        Long senderId
    ) {
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomIdAndDeletedAtNull(chatRoomId)
            .orElseThrow(() -> new CustomException(CHAT_ROOM_NOT_FOUND));

        Boolean participantExists = chatRoom.getChatRoomParticipants().stream()
            .anyMatch(participant -> participant.getUserId().equals(senderId));
        log.info("participantExists {}", participantExists);

        Map<String, Boolean> response = new HashMap<>();
        response.put("valid", participantExists);

        return response;
    }

    /**
     * 소켓 연결 중단 시, redis 삭제 호출
     * Redis key(chatRoomId:senderId)-value(SessionID, senderId, chatRoomId)
     */
    @Transactional(readOnly = true)
    public Boolean deleteRedisSenderId(
        UUID chatRoomId,
        String senderId
    ) {
        String cacheKey = chatRoomId + ":"+ senderId;
        log.info("cacheKey: {}", cacheKey);

        if(redisTemplate.hasKey(cacheKey)) {
            log.info("소켓 연결 해지로 cacheKey={}인 Redis 캐시 삭제", cacheKey);
            redisTemplate.delete(cacheKey);
            return true;
        } else {
            log.info("cacheKey={}에 해당하는 Redis 캐시가 존재하지 않음", cacheKey);
            return false;
        }
    }

    /**
     * mongoDB 에서 채팅방(chatRoomId) 이전 메시지 불러오기(조회)
     * Page 전체 조회(sentAt ASC 정렬)
     */
    @Transactional(readOnly = true)
    public Page<ChatMessageDocument> getMessagesByChatRoomId(UUID chatRoomId, Long senderId, Pageable pageable) {
        // 채팅방 id 및 userId 유효성 검증
        checkRoomIdAndUserId(chatRoomId, senderId);

        // 삭제되지 않은 메시지만 조회
        Query query = Query.query(
            Criteria.where("chatRoomId").is(chatRoomId)
                .and("deletedAt").is(null)
        ).with(pageable);

        long total = mongoTemplate.count(query, ChatMessageDocument.class);
        if(total == 0) {
            log.error("채팅방에 해당하는 메시지 내역이 존재하지 않습니다.");
        }

        List<ChatMessageDocument> messageDocumentList = mongoTemplate.find(query, ChatMessageDocument.class);

        return new PageImpl<>(messageDocumentList, pageable, total);
    }

    /**
     * 채팅방 메시지 상세 조회
     * chatRoomId에 속하는 참여자는 메시지 고유 ID로 메시지 상세 조회 모두 가능
     */
    @Transactional(readOnly = true)
    public ChatMessageResponseDto getMessageById(UUID chatRoomId, String id, Long userId) {
        // 채팅방 id 및 userId 유효성 검증
        checkRoomIdAndUserId(chatRoomId, userId);

        Query query = Query.query(Criteria.where("_id").is(id));
        ChatMessageDocument responseDocument = mongoTemplate.findOne(query, ChatMessageDocument.class);

        if(responseDocument == null) {
            log.error("chatRoomId: {} 해당 채팅방의 id:{} 메시지가 존재하지 않습니다.", chatRoomId, id);
            throw new CustomException(MESSAGE_ID_NOT_FOUND);
        }

        return responseDocument.toResponse();
    }

    /**
     * "/ws-stomp" 경로로 소켓 연결 시, 채팅방에 참여한 user가 Redis 캐시 조회하여 있는 경우 소켓 연결 중단
     * 서버 리셋 후, 다시 소켓 연결하면 재접속 가능
     * 웹 소켓 연결 시, Redis key(chatRoomId:senderId)-value(SessionID, senderId, chatRoomId)
     * 이전 메시지 조회로 채팅방에 처음 입장한 경우, 입장 메시지 전송
     */
    @Transactional
    public void connectChatRoom(
        UUID chatRoomId,
        Long senderId,
        SimpMessageHeaderAccessor headerAccessor,
        String message
    ) {
        String sessionId = headerAccessor.getSessionId();
        log.info("connectChatRoom sessionId: {}", sessionId);

        String cacheKey = chatRoomId + ":"+ senderId;
        log.info("cacheKey: {}", cacheKey);

        // Redis에 senderId:chatRoomId 이미 존재하는지 확인
        if(redisTemplate.hasKey(cacheKey)) {
            // 이미 존재하는 경우, 중복 연결 차단 메시지 전송 및 senderId가 연결된 소켓 연결 중단
            log.warn("cacheKey {} 가 이미 존재하여 중복 연결이 차단됨", cacheKey);
            redisTemplate.delete(cacheKey);
            messagingTemplate.convertAndSend("/topic/v1/chat/errors/" + senderId,
                "중복 로그인으로 인해 연결이 거부되었습니다. 해당 채팅방에 다시 접속해주세요.");
        } else {
            // 존재하지 않은 경우, Redis에 senderId, chatRoomId 저장
            Map<String, Object> value = new HashMap<>();
            value.put("sessionId", sessionId);
            value.put("chatRoomId", chatRoomId);
            value.put("senderId", senderId);

            redisTemplate.opsForHash().putAll(cacheKey, value);
            redisTemplate.expire(cacheKey, 1, TimeUnit.DAYS);

            log.info("[Redis 캐싱] cacheKey={} 과 value={} 저장", cacheKey, value);

            // mongoDB에서 userId와 chatRoomId에 대한 메시지 유무로 처음인지 판단하고 입장 메시지 전송
            Query query = Query.query(
                Criteria.where("chatRoomId").is(chatRoomId)
                    .and("senderId").is(senderId)
                    .and("deletedAt").is(null));

            long messageCount = mongoTemplate.count(query, ChatMessageDocument.class);
            log.info(">> messageCount={}", messageCount);

            if(messageCount == 0) {
                // chatRoomId에서 senderId가 보낸 메시지가 없는 경우, mongoDB 메시지 저장
                ChatMessageDocument firstMessage = ChatMessageDocument.builder()
                    .chatRoomId(chatRoomId)
                    .senderId(senderId)
                    .connectionType(ConnectionType.ENTER)
                    .chatMessageType(ChatMessageType.TEXT)
                    .messageContent(message)
                    .sentAt(LocalDateTime.now())
                    .build();
                try {
                    // 처음 채팅방 접속으로 입장 메시지 전송
                    ChatMessageDocument savedMessage = chatMessageRepository.save(firstMessage);
                    log.info("[mongoDB 저장 성공] message={}", savedMessage.toString());
                    messagingTemplate.convertAndSend("/topic/v1/chat/enter/" + chatRoomId, savedMessage);
                } catch (Exception e) {
                    log.error("[mongoDB 저장 실패] message={}", firstMessage, e);
                }
            }
        }
    }

    /**
     * stomp 메시지 브로커를 통한 메시지 전송
     */
    @Transactional
    public ChatMessageResponseDto sendMessage(ChatMessageRequestDto requestDto) {
        log.info("[채팅 비즈니스 sendMessage 메소드 시작] requestDto={}", requestDto);
        // 메시지 저장 및 전달
        ChatMessageDocument chatMessage = ChatMessageDocument.builder()
            .chatRoomId(requestDto.getChatRoomId())
            .senderId(requestDto.getSenderId())
            .receiverId(requestDto.getReceiverId())
            .receiverNickname(requestDto.getReceiverNickname())
            .connectionType(ConnectionType.CHAT)
            .chatMessageType(ChatMessageType.TEXT)
            .messageContent(requestDto.getMessageContent())
            .sentAt(LocalDateTime.now())
            .build();
        chatMessageRepository.save(chatMessage);
        log.info("chatMessageContent={}", chatMessage.getMessageContent());
        log.info("ReceiverNickname={}", requestDto.getReceiverNickname());

        // 수정이 필요한 사항 : 현재 보내는 사람의 닉네임을 받을 수 없어 임시로 받는 사람의 닉네임을 보냄. 추후 수정 필요
        notificationKafkaProducerService.sendChatCreate(ChatNotificationEventDto
                .from(requestDto.getReceiverId(), requestDto.getReceiverNickname(), requestDto.getMessageContent()));

        return chatMessage.toResponse();
    }

    // 채팅방 id 및 userId 유효성 검증
    private void checkRoomIdAndUserId(UUID chatRoomId, Long userId) {
        // 채팅방 유무 검증
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomIdAndDeletedAtNull(chatRoomId)
            .orElseThrow(() -> new CustomException(CHAT_ROOM_NOT_FOUND));

        // 채팅방 참여자만 해당 메시지 조회 가능
        boolean isParticipant = chatRoom.getChatRoomParticipants().stream()
            .anyMatch(participant -> participant.getUserId().equals(userId));

        if(!isParticipant) {
            throw new CustomException(CHAT_ROOM_USER_ID_NOT_FOUND);
        }
    }

    // 채팅방의 메시지 소프트 삭제
    private void deleteChatMessageForChatRoomId(UUID chatRoomId, Long userId) {
        log.info(">> 메시지 소프트 삭제 시작");

        // 채팅방의 모든 메시지 조회
        Query query = Query.query(
            Criteria.where("chatRoomId").is(chatRoomId)
                .and("deletedAt").is(null));

        List<ChatMessageDocument> chatMessages = mongoTemplate.find(query, ChatMessageDocument.class);
        log.info("chatMessages.size() = {}", chatMessages.size());

        if (chatMessages.isEmpty()) {
            log.error("채팅방에 해당하는 메시지 내역이 존재하지 않습니다.");
//            throw new CustomException(MESSAGE_NOT_FOUND_FOR_CHAT_ROOM);
        } else {
            // 메시지 소프트 삭제
            chatMessages.forEach(chatMessage -> {
                log.info("[소프트삭제 처리]chatMessage.getSenderId() = {}", chatMessage.getSenderId());
                chatMessage.softDelete(userId);
                chatMessageRepository.save(chatMessage);
            });
            log.info(">> 메시지 소프트 삭제 완료 : 채팅방 삭제에 따른 채팅방/참여자/메시지 삭제 완료");
        }
    }
}
