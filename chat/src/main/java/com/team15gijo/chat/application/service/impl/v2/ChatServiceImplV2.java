package com.team15gijo.chat.application.service.impl.v2;

import static com.team15gijo.chat.domain.exception.ChatDomainExceptionCode.CHAT_ROOM_INDIVIDUAL_NUMBER_LIMIT;
import static com.team15gijo.chat.domain.exception.ChatDomainExceptionCode.CHAT_ROOM_NOT_FOUND;
import static com.team15gijo.chat.domain.exception.ChatDomainExceptionCode.CHAT_ROOM_USER_ID_NOT_FOUND;
import static com.team15gijo.chat.domain.exception.ChatDomainExceptionCode.MESSAGE_ID_NOT_FOUND;
import static com.team15gijo.chat.domain.exception.ChatDomainExceptionCode.USER_NICK_NAME_NOT_EXIST;
import static com.team15gijo.chat.domain.model.v2.ChatMessageDocumentV2.createChatMessageDocument;
import static com.team15gijo.chat.domain.model.v2.ChatMessageDocumentV2.createEnterMessageDocument;
import static com.team15gijo.chat.domain.model.v2.ChatMessageDocumentV2.createErrorMessageDocument;

import com.team15gijo.chat.application.dto.v2.ChatMessageResponseDtoV2;
import com.team15gijo.chat.application.dto.v2.ChatRoomResponseDtoV2;
import com.team15gijo.chat.application.service.ChatServiceV2;
import com.team15gijo.chat.domain.model.v2.ChatMessageDocumentV2;
import com.team15gijo.chat.domain.model.v2.ChatRoomParticipantV2;
import com.team15gijo.chat.domain.model.v2.ChatRoomTypeV2;
import com.team15gijo.chat.domain.model.v2.ChatRoomV2;
import com.team15gijo.chat.domain.repository.v2.ChatMessageRepositoryV2;
import com.team15gijo.chat.domain.repository.v2.ChatRoomParticipantRepositoryV2;
import com.team15gijo.chat.domain.repository.v2.ChatRoomRepositoryV2;
import com.team15gijo.chat.infrastructure.client.v1.FeignClientService;
import com.team15gijo.chat.infrastructure.kafka.util.KafkaUtil;
import com.team15gijo.chat.presentation.dto.v2.ChatMessageRequestDtoV2;
import com.team15gijo.chat.presentation.dto.v2.ChatRoomRequestDtoV2;
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
import java.util.stream.Collectors;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImplV2 implements ChatServiceV2 {
    private final ChatMessageRepositoryV2 chatMessageRepository;
    private final ChatRoomRepositoryV2 chatRoomRepository;
    private final ChatRoomParticipantRepositoryV2 chatRoomParticipantRepository;

    private final FeignClientService feignClientService;

    private final RedisTemplate<String, Object> redisTemplate;
    private final MongoTemplate mongoTemplate;

    private static final String CHAT_MESSAGE_EVENT_TOPIC = "chat_message_event";
    private static final String CHAT_ERROR_EVENT_TOPIC = "chat_error_event";
    private final KafkaUtil kafkaUtil;

    /**
     * 1차 MVP 1:1 채팅만 구현
     * 채팅방 생성(chatRoomType, receiver)에 따른 채팅방 참여자 생성
     * UserFeignClient 사용자 유효성 검사
     */
    @Override
    @Transactional
    public ChatRoomResponseDtoV2 createChatRoom(
        ChatRoomRequestDtoV2 requestDto,
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
        ChatRoomParticipantV2 addParticipants = ChatRoomParticipantV2.builder()
            .userId(userId)
            .activation(Boolean.TRUE)
            .build();
        chatRoomParticipantRepository.save(addParticipants);

        Set<ChatRoomParticipantV2> participantsSet = new HashSet<>();
        participantsSet.add(addParticipants);

        // 초대받은 참여자의 채팅방 참여자 생성 및 저장
        ChatRoomParticipantV2 invitedParticipant = ChatRoomParticipantV2.builder()
            .userId(getUserIdByNickname)
            .activation(Boolean.TRUE)
            .build();
        chatRoomParticipantRepository.save(invitedParticipant);
        participantsSet.add(invitedParticipant);

        // 채팅방 생성 및 저장
        ChatRoomV2 chatRoom = ChatRoomV2.builder()
            .chatRoomType(requestDto.getChatRoomType())
            .chatRoomParticipant(participantsSet)
            .build();
        ChatRoomV2 savedChatRoom = chatRoomRepository.save(chatRoom);

        // 채팅방 타입이 INDIVIDUAL 인 경우, 참여자 수 제한
        if(savedChatRoom.getChatRoomType() == ChatRoomTypeV2.INDIVIDUAL
            && savedChatRoom.getChatRoomParticipant().size() > 2) {
            throw new CustomException(CHAT_ROOM_INDIVIDUAL_NUMBER_LIMIT);
        }
        return savedChatRoom.toResponse();
    }

    /**
     * 채팅방 단일 조회
     */
    @Override
    @Transactional(readOnly = true)
    public ChatRoomResponseDtoV2 getChatRoom(UUID chatRoomId, Long userId) {
        ChatRoomV2 chatRoom = chatRoomRepository.findById(chatRoomId).
            orElseThrow(() -> new CustomException(CHAT_ROOM_NOT_FOUND));

        if(chatRoom.getChatRoomParticipant().stream()
            .anyMatch(participant -> participant.getUserId().equals(userId))) {
            return chatRoom.toResponse();
        } else {
            throw new CustomException(CHAT_ROOM_USER_ID_NOT_FOUND);
        }
    }

    /**
     * 채팅방 전체 조회
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ChatRoomV2> getChatRooms(Pageable pageable, Long userId) {
        log.info("userId = {}", userId);
        List<ChatRoomV2> chatRoomList = chatRoomRepository.findAll();

        List<ChatRoomV2> filteredChatRooms = chatRoomList.stream()
            .filter(chatRoom -> chatRoom.getChatRoomParticipant().stream()
                .anyMatch(participant -> participant.getUserId().equals(userId)))
            .toList();

        if(filteredChatRooms.isEmpty()) {
            throw new CustomException(CHAT_ROOM_NOT_FOUND);
        }

        // 페이지네이션 적용
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filteredChatRooms.size());
        List<ChatRoomV2> pageContent = filteredChatRooms.subList(start, end);

        return new PageImpl<>(pageContent, pageable, pageable.getPageSize());
    }

    /**
     * 채팅방 퇴장(비활성화) -> 삭제(Batch 또는 비동기 처리)
     * -> 1:1 채팅방에서 각 1명의 사용자 퇴장(채팅방 참여자 비활성화로 변경)
     * -> 채팅방의 모든 참여자 퇴장 시, 채팅방 & 채팅방참여자 & 채팅메시지 모두 소프트 삭제
     */
    @Override
    @Transactional
    public boolean exitChatRoom(UUID chatRoomId, Long userId) {
        log.info("[ChatServiceImplV2 - exitChatRoom] chatRoomId = {}", chatRoomId);
        log.info("[ChatServiceImplV2 - exitChatRoom] userId = {}", userId);

        ChatRoomV2 chatRoom = chatRoomRepository.findByChatRoomId(chatRoomId)
            .orElseThrow(() -> new CustomException(CHAT_ROOM_NOT_FOUND));

        Set<ChatRoomParticipantV2> chatRoomParticipants = chatRoom.getChatRoomParticipant();
        ChatRoomParticipantV2 targetParticipant = null;
        log.info("[ChatServiceImplV2 - 존재하는 참여자 수] chatRoomParticipants.size() = {}", chatRoomParticipants.size());

        // 퇴장 요청 참여자 찾기
        for(ChatRoomParticipantV2 participant : chatRoomParticipants) {
            if(participant.getUserId().equals(userId)) {
                log.info("[ChatServiceImplV2] participant.getUserId() = {}", participant.getUserId());
                log.info("[ChatServiceImplV2] getActivation() 전 = {}", participant.getActivation());
                participant.nonActivate();
                log.info("[ChatServiceImplV2] getActivation() 후 = {}", participant.getActivation());

                targetParticipant = participant;
                break;
            }
        }

        // 비활성화된 참여자 있다면 저장
        if(targetParticipant != null) {
            log.info("[ChatServiceImplV2] 퇴장 원하는 사용자 비활성화 업데이트");
            chatRoomParticipantRepository.save(targetParticipant);
        }

        // 모든 참여자가 비활성 상태인지 확인 -> 모두 비활성화면 return TRUE
        boolean allParticipantsNonactive = chatRoomParticipants.stream()
            .allMatch(participant -> !participant.getActivation());

        // 모든 채팅방 참여자가 비활성화 상태면 채팅방 및 참여자 소프트삭제 처리
        if(allParticipantsNonactive) {
            log.info("[ChatServiceImplV2] 채팅방 참여자 비활성화 상태 - allParticipantsNonactive={}", allParticipantsNonactive);

            // 비활성화된 참여자 List
            List<ChatRoomParticipantV2> nonActiveParticipants = chatRoomParticipants.stream()
                .filter(participant -> !participant.getActivation())
                .toList();

            // bulk 로 쿼리문 1개만 사용하여 참여자 모두 삭제 처리
            if(!nonActiveParticipants.isEmpty()) {
                log.info("[ChatServiceImplV2] 비활성화된 참여자 {}명", nonActiveParticipants.size());
                log.info("[ChatServiceImplV2] 비활성화된 참여자 소프트 삭제 전");
                log.info("[ChatServiceImplV2] nonActiveParticipants={}, userId={}", nonActiveParticipants, userId);
                chatRoomParticipantRepository.softDeleteAllInBatch(
                    nonActiveParticipants, userId, LocalDateTime.now()
                );
                log.info("[ChatServiceImplV2] 비활성화된 참여자 일괄 소프트 삭제 완료");
            }

            // chatRoomParticipant 소프트 삭제 이후, chatRoom 소프트 삭제 처리
            // TODO: 해당 PATCH 로직에서 채팅방 참여자 비활성화만 진행하고, 비동기 또는 배치로 채팅방 삭제 구현 필요!
            log.info("[ChatServiceImplV2] chatRoom={} 채팅방 소프트 삭제 전", chatRoom);
            chatRoomRepository.delete(chatRoom);
            log.info("[ChatServiceImplV2] updatedChatRoom={} 채팅방 소프트 삭제 완료", chatRoom);

            // 채팅방 모든 참여자 userId 메시지 소프트 삭제 메소드
            deleteChatMessageForChatRoomId(chatRoomId, userId);

            log.info("[ChatServiceImplV2] 채팅방 삭제에 따른 채팅방/참여자/메시지 소프트 삭제 모두 성공");
            return true;
        } else {
            log.info("[ChatServiceImplV2] 채팅방 참여자 일부 활성화 상태로 삭제 불가 - allParticipantsNonactive={}", allParticipantsNonactive);
            return false;
        }
    }

    /**
     * 수신자 닉네임 검증 및 웹소켓 연결 시, 발송자 닉네임 전달
     */
    @Override
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
    @Override
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
    @Override
    @Transactional(readOnly = true)
    public Map<String, Boolean> validateSenderId(
        UUID chatRoomId,
        Long senderId
    ) {
        ChatRoomV2 chatRoom = chatRoomRepository.findByChatRoomId(chatRoomId)
            .orElseThrow(() -> new CustomException(CHAT_ROOM_NOT_FOUND));

        Boolean participantExists = chatRoom.getChatRoomParticipant().stream()
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
    @Override
    @Transactional(readOnly = true)
    public Boolean deleteRedisSenderId(
        UUID chatRoomId,
        String senderId
    ) {
        log.info("[ChatServiceImplV2] deleteRedisSenderId 메소드 시작");
        String cacheKey = "CHATROOM:" + chatRoomId + ":"+ senderId;
        log.info("cacheKey: {}", cacheKey);

        if(redisTemplate.hasKey(cacheKey)) {
            log.info("소켓 연결 해지 요청으로 cacheKey={}인 Redis 캐시 삭제", cacheKey);
            redisTemplate.delete(cacheKey);
            log.info("[ChatServiceImplV2] deleteRedisSenderId 메소드 종료");
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
    @Override
    @Transactional(readOnly = true)
    public Page<ChatMessageDocumentV2> getMessagesByChatRoomId(UUID chatRoomId, Long senderId, Pageable pageable) {
        // 채팅방 id 및 userId 유효성 검증
        checkRoomIdAndUserId(chatRoomId, senderId);

        // 삭제되지 않은 메시지만 조회
        Query query = Query.query(
            Criteria.where("chatRoomId").is(chatRoomId)
                .and("deletedAt").is(null)
        ).with(pageable);

        long total = mongoTemplate.count(query, ChatMessageDocumentV2.class);
        if(total == 0) {
            log.error("채팅방에 해당하는 메시지 내역이 존재하지 않습니다.");
        }

        List<ChatMessageDocumentV2> messageDocumentList = mongoTemplate.find(query, ChatMessageDocumentV2.class);

        return new PageImpl<>(messageDocumentList, pageable, total);
    }

    /**
     * 채팅방 메시지 상세 조회
     * chatRoomId에 속하는 참여자는 메시지 고유 ID로 메시지 상세 조회 모두 가능
     */
    @Override
    @Transactional(readOnly = true)
    public ChatMessageResponseDtoV2 getMessageById(UUID chatRoomId, String id, Long userId) {
        // 채팅방 id 및 userId 유효성 검증
        checkRoomIdAndUserId(chatRoomId, userId);

        Query query = Query.query(Criteria.where("_id").is(id));
        ChatMessageDocumentV2 responseDocument = mongoTemplate.findOne(query, ChatMessageDocumentV2.class);

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
    @Override
    @Transactional
    public void connectChatRoom(
        UUID chatRoomId, Long senderId, SimpMessageHeaderAccessor headerAccessor) {
        log.info("[ChatServiceImplV2] connectChatRoom 메소드 시작");

        String sessionId = headerAccessor.getSessionId();
        String cacheKey = "CHATROOM:" + chatRoomId + ":"+ senderId;

        // Redis에 chatRoomId:senderId가 이미 존재하는지 확인
        if(redisTemplate.hasKey(cacheKey)) {
            // 이미 존재하는 경우, 중복 연결 차단 메시지 전송 및 senderId가 연결된 소켓 연결 중단
            log.warn("cacheKey={} 가 이미 존재하여 중복 연결 차단 및 해당 캐시 삭제", cacheKey);
            redisTemplate.delete(cacheKey);

            // 카프카 에러 메시지 처리
            String errorMessage = "중복 로그인으로 인해 연결이 거부되었습니다. 해당 채팅방에 다시 접속해주세요.";
            ChatMessageDocumentV2 errorDocument = createErrorMessageDocument(chatRoomId, senderId, errorMessage);
            kafkaUtil.sendKafkaEvent(CHAT_ERROR_EVENT_TOPIC, errorDocument);
            log.info("[ChatServiceImplV2] redis 키 중복으로 웹소켓 중복 연결 차단 - Kafka 발행 완료");
        } else {
            // 존재하지 않은 경우, Redis에 chatRoomId:senderId 저장
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

            long messageCount = mongoTemplate.count(query, ChatMessageDocumentV2.class);

            log.info("[채팅방({})의 사용자({}) 메시지 내역 조회] messageCount={}", chatRoomId, senderId, messageCount);

            if(messageCount == 0) {
                // chatRoomId에서 senderId가 보낸 메시지가 없는 경우, 입장 메시지 전송하기
                String messageContent = senderId + "님이 입장하였습니다.";
                Long receiverId = Long.parseLong(headerAccessor.getSessionAttributes().get("receiverId").toString());
                String receiverNickname = headerAccessor.getSessionAttributes().get("receiverNickname").toString();

                ChatMessageDocumentV2 firstMessage = createEnterMessageDocument(chatRoomId, senderId, receiverId, receiverNickname, messageContent);

                // 카프카 입장 메시지 처리
                kafkaUtil.sendKafkaEvent(CHAT_MESSAGE_EVENT_TOPIC, firstMessage);
                log.info("[ChatServiceImplV2] connectChatRoom 메소드 종료 - Kafka 발행 완료");
            }
        }
    }

    /**
     * stomp 메시지 브로커를 통한 메시지 전송
     */
    @Override
    @Transactional
    public void sendMessage(
        ChatMessageRequestDtoV2 requestDto,
        SimpMessageHeaderAccessor headerAccessor) {
        log.info("[ChatServiceImplV2] sendMessage 메소드 시작");

        // 메시지 변환
        ChatMessageDocumentV2 chatMessage = createChatMessageDocument(requestDto);

        // 카프카 채팅 메시지 처리
        kafkaUtil.sendKafkaEvent(CHAT_MESSAGE_EVENT_TOPIC, chatMessage);
        log.info("[ChatServiceImplV2] sendMessage 메소드 종료 - Kafka 발행 완료");
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChatMessageDocumentV2> searchMessages(UUID chatRoomId, LocalDateTime sentAt, String messageContent, Long userId, Pageable pageable) {
        log.info("[ChatServiceImplV2] searchMessages 메소드 시작");
        // 채팅방 id 및 userId 유효성 검증
        checkRoomIdAndUserId(chatRoomId, userId);

        return chatMessageRepository.searchMessages(chatRoomId, sentAt, messageContent, pageable);
    }

    // 채팅방 id 및 userId 유효성 검증
    private void checkRoomIdAndUserId(UUID chatRoomId, Long userId) {
        // 채팅방 유무 검증
        ChatRoomV2 chatRoom = chatRoomRepository.findByChatRoomId(chatRoomId)
            .orElseThrow(() -> new CustomException(CHAT_ROOM_NOT_FOUND));

        // 채팅방 참여자만 해당 메시지 조회 가능
        boolean isParticipant = chatRoom.getChatRoomParticipant().stream()
            .anyMatch(participant -> participant.getUserId().equals(userId));

        if(!isParticipant) {
            throw new CustomException(CHAT_ROOM_USER_ID_NOT_FOUND);
        }
    }

    // 채팅방의 메시지 소프트 삭제
    private void deleteChatMessageForChatRoomId(UUID chatRoomId, Long userId) {
        log.info("[ChatServiceImplV2] deleteChatMessageForChatRoomId - 메시지 소프트 삭제 전");
        // 채팅방의 모든 메시지 조회
        Query query = Query.query(
            Criteria.where("chatRoomId").is(chatRoomId)
                .and("deletedAt").is(null));

        List<ChatMessageDocumentV2> chatMessages = mongoTemplate.find(query, ChatMessageDocumentV2.class);
        log.info("[ChatServiceImplV2] chatMessages.size()={}", chatMessages.size());

        if (chatMessages.isEmpty()) {
            log.error("채팅방에 해당하는 메시지 내역이 존재하지 않습니다.");
        } else {
            // 메시지 소프트 삭제
            chatMessages.forEach(chatMessage -> {
                chatMessage.softDelete(userId);
                chatMessageRepository.save(chatMessage);
            });
            log.info("[ChatServiceImplV2] deleteChatMessageForChatRoomId - 메시지 일괄 소프트 삭제 완료");
        }
    }
}
