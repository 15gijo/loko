package com.team15gijo.chat.application.service.impl.v1;

import static com.team15gijo.chat.domain.exception.ChatDomainExceptionCode.CHAT_ROOM_INDIVIDUAL_NUMBER_LIMIT;
import static com.team15gijo.chat.domain.exception.ChatDomainExceptionCode.CHAT_ROOM_NOT_FOUND;
import static com.team15gijo.chat.domain.exception.ChatDomainExceptionCode.CHAT_ROOM_USER_ID_NOT_FOUND;
import static com.team15gijo.chat.domain.exception.ChatDomainExceptionCode.MESSAGE_ID_NOT_FOUND;
import static com.team15gijo.chat.domain.exception.ChatDomainExceptionCode.MESSAGE_NOT_FOUND_FOR_CHAT_ROOM;

import com.team15gijo.chat.application.dto.v1.ChatRoomResponseDto;
import com.team15gijo.chat.domain.model.ChatMessageDocument;
import com.team15gijo.chat.domain.model.ChatMessageType;
import com.team15gijo.chat.domain.model.ChatRoom;
import com.team15gijo.chat.domain.model.ChatRoomParticipant;
import com.team15gijo.chat.domain.model.ChatRoomType;
import com.team15gijo.chat.domain.model.ConnectionType;
import com.team15gijo.chat.domain.repository.ChatMessageRepository;
import com.team15gijo.chat.domain.repository.ChatRoomParticipantRepository;
import com.team15gijo.chat.domain.repository.ChatRoomRepository;
import com.team15gijo.chat.infrastructure.client.v1.FeignClientService;
import com.team15gijo.chat.presentation.dto.v1.ChatMessageRequestDto;
import com.team15gijo.chat.presentation.dto.v1.ChatMessageResponseDto;
import com.team15gijo.chat.presentation.dto.v1.ChatRoomParticipantRequestDto;
import com.team15gijo.chat.presentation.dto.v1.ChatRoomParticipantResponseDto;
import com.team15gijo.chat.presentation.dto.v1.ChatRoomRequestDto;
import com.team15gijo.common.exception.CustomException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestHeader;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomParticipantRepository chatRoomParticipantRepository;

    private final FeignClientService feignClientService;

    private final RedisTemplate<String, String> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    private final MongoTemplate mongoTemplate;

    /**
     * 1차 MVP 1:1 채팅만 구현
     * 채팅방 생성(chatRoomType, receiver)에 따른 채팅방 참여자 생성
     * UserFeignClient 사용자 유효성 검사
     */
    public ChatRoomResponseDto createChatRoom(
        ChatRoomRequestDto requestDto,
        Long userId,
        String nickname
    ) {
        log.info("userId = {}", userId);
        log.info("nickname = {}", nickname);

        //TODO: User feign client 유효성 검사 구현 후, 연결 확인
        // 채팅방 생성 시, 상대방 계정 조회(nickname 으로 존재유무 판단)
        // 상대방과 본인 userId를 모두 추출하여 채팅방 참여자 생성하기!
        String receiverNickname = requestDto.getReceiverNickname();
//        Long getUserIdByNickname = feignClientService.fetchUserIdByNickname(receiverNickname);
//        if(getUserIdByNickname == null) {
//            throw new CustomException(USER_NICK_NAME_NOT_EXIST);
//        }

        // 채팅방 생성하는 당사자의 참여자 생성 및 저장
        ChatRoomParticipant addParticipants = ChatRoomParticipant.builder()
            .userId(userId)
            .activation(Boolean.TRUE)
            .build();
        chatRoomParticipantRepository.save(addParticipants);

        Set<ChatRoomParticipant> participantsSet = new HashSet<>();
        participantsSet.add(addParticipants);

//        // 초대받은 참여자의 채팅방 참여자 생성 및 저장
//        ChatRoomParticipant invitedParticipant = ChatRoomParticipant.builder()
//            .userId(getUserIdByNickname)
//            .activation(Boolean.TRUE)
//            .build();
//        chatRoomParticipantRepository.save(invitedParticipant);
//        participantsSet.add(invitedParticipant);

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
     * TODO: 마지막 참여자 activation false 변경 안됨 & 메시지 소프트 삭제 처리 필요
     */
    public boolean exitChatRoom(UUID chatRoomId, Long userId) {
        log.info("[exitChatRoom] chatRoomId = {}", chatRoomId);
        log.info("[exitChatRoom] userId = {}", userId);

        ChatRoom chatRoom = chatRoomRepository.findByChatRoomId(chatRoomId)
            .orElseThrow(() -> new CustomException(CHAT_ROOM_NOT_FOUND));

        // 퇴장 요청 참여자 비활성화
        Set<ChatRoomParticipant> chatRoomParticipants = chatRoom.getChatRoomParticipants().stream()
            .map(participant -> {
                if (participant.getUserId().equals(userId)) {
                    log.info("[exitChatRoom] userId = {}", participant.getUserId());
                    log.info("[exitChatRoom] getActivation() 전 = {}", participant.getActivation());
                    participant.nonActivate();
                    log.info("[exitChatRoom] getActivation() 후 = {}", participant.getActivation());

                    return chatRoomParticipantRepository.save(participant);
                } else {
                    return participant;
                }
            })
            .collect(Collectors.toSet());

        // 변경된 참여자로 ChatRoom 업데이트
        ChatRoom updatedChatRoom = ChatRoom.builder()
            .chatRoomId(chatRoomId)
            .chatRoomType(chatRoom.getChatRoomType())
            .chatRoomParticipants(chatRoomParticipants)
            .build();
        chatRoomRepository.save(updatedChatRoom);

        // 모든 참여자가 비활성 상태인지 확인 -> 모두 비활성화면 return TRUE
        boolean allParticipantsNonactive = chatRoomParticipants.stream()
            .allMatch(participant -> !participant.getActivation());
        log.info("[exitChatRoom] allParticipantsNonactive = {}", allParticipantsNonactive);

        // 모든 채팅방 참여자가 비활성화 상태면 채팅방 및 참여자 소프트삭제 처리
        if(allParticipantsNonactive) {
            log.info("[exitChatRoom] 모든 채팅방 참여자 비활성화 상태");
            chatRoomRepository.delete(chatRoom);
            chatRoomParticipantRepository.deleteAll(chatRoomParticipants);

            // TODO: 채팅방 참여자 userId 1명의 메시지만 삭제됨 + 상대방 userId 메시지도 삭제 처리

            return true;
        } else {
            return false;
        }
    }

    /**
     * 채팅방에 상대방 참여자 입장(접속) -> 채팅방 생성과 동시에 참여자 전원 채팅방참여자 생성 구현함
     * 그러나, User feign client로 상대방 닉네임으로 userId 조회하여 참여자 등록 전까지
     * TODO: 현재 참여자 등록으로 테스트 진행 예정 -> 최종 구현 이후 삭제 예정
     * @param request(userId, chatRoomId)
     * @return
     */
    public ChatRoomParticipantResponseDto addChatParticipant(
        ChatRoomParticipantRequestDto request,
        @RequestHeader("X-User-Id") Long userId,
        @RequestHeader("X-User-Nickname") String nickname
    ) {
        log.info("userId = {}", userId);
        log.info("nickname = {}", nickname);

        ChatRoom findChatRoom = chatRoomRepository.findByChatRoomId(request.getChatRoomId())
            .orElseThrow(() -> new CustomException(CHAT_ROOM_NOT_FOUND));

        ChatRoomParticipant participant = ChatRoomParticipant.builder()
            .userId(userId)
            .activation(Boolean.TRUE)
            .build();
        ChatRoomParticipant savedParticipant = chatRoomParticipantRepository.save(participant);

        Set<ChatRoomParticipant> participantSet = findChatRoom.getChatRoomParticipants();
        participantSet.add(participant);

        // 채팅방 생성 및 저장
        ChatRoom chatRoom = ChatRoom.builder()
            .chatRoomId(request.getChatRoomId())
            .chatRoomType(findChatRoom.getChatRoomType())
            .chatRoomParticipants(participantSet)
            .build();
        chatRoomRepository.save(chatRoom);

        return savedParticipant.toResponse();
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
    public Map<String, Boolean> validateSenderId(UUID chatRoomId, Long senderId) {
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomId(chatRoomId)
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
     * TODO: Redis 키를 SessionID로, value는 senderId, chatRoomId로 변경
     */
    @Transactional(readOnly = true)
    public Boolean deleteRedisSenderId(Long senderId) {
        if(redisTemplate.hasKey(String.valueOf(senderId))) {
            log.info("소켓 연결 해지로 senderId {}의 Redis 캐시 삭제", senderId);
            redisTemplate.delete(String.valueOf(senderId));
            return true;
        } else {
            log.info("senderId {}에 해당하는 Redis 캐시가 존재하지 않음", senderId);
            return false;
        }
    }

    /**
     * mongoDB 에서 채팅방(chatRoomId) 이전 메시지 불러오기(조회)
     * Page 전체 조회(sentAt ASC 정렬)
     */
    @Transactional(readOnly = true)
    public Page<ChatMessageDocument> getMessagesByChatRoomId(UUID chatRoomId, Long senderId, Pageable pageable) {
        checkRoomIdAndUserId(chatRoomId, senderId);
        /*
          chatRoomId가 UUID 타입으로 mongoDB 에서 바이너리 형태로 저장되어
          쿼리에서 바이너리 데이터를 직접 비교하고 인덱싱, 조회가 제대로 되지 않음
          고유 id 값을 추출하여 데이터 조회
         */
        List<String> idList = chatMessageRepository.findAll().stream()
            .filter(chatMessage -> chatMessage.getChatRoomId().equals(chatRoomId))
            .map(ChatMessageDocument::get_id)
            .toList();
        log.info("idList {}", idList);

        if(idList.isEmpty()) {
            log.error("채팅방에 해당하는 메시지 내역이 존재하지 않습니다.");
            throw new CustomException(MESSAGE_NOT_FOUND_FOR_CHAT_ROOM);
        }

        Query query = Query.query(Criteria.where("_id").in(idList))
            .with(pageable);

        long total = mongoTemplate.count(query, ChatMessageDocument.class);
        List<ChatMessageDocument> messageDocumentList = mongoTemplate.find(query, ChatMessageDocument.class);

        return new PageImpl<>(messageDocumentList, pageable, total);
    }

    /**
     * 채팅방 메시지 상세 조회
     * chatRoomId에 속하는 참여자는 메시지 고유 ID로 메시지 상세 조회 모두 가능
     */
    @Transactional(readOnly = true)
    public ChatMessageResponseDto getMessageById(UUID chatRoomId, String id, Long userId) {
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
     * 웹 소켓 연결 시, redis senderId(key)-sessionId(value) 캐시 저장
     * -> TODO: Redis 키를 SessionID로, value는 senderId, chatRoomId로 변경
     * 이전 메시지 조회로 채팅방에 처음 입장한 경우, 입장 메시지 전송
     */
    public void connectChatRoom(
        UUID chatRoomId,
        Long senderId,
        SimpMessageHeaderAccessor headerAccessor,
        String message
    ) {
        if (senderId != null) {
            // 세션 ID 가져오기
            String sessionId = headerAccessor.getSessionId();
            log.info("connectChatRoom sessionId: {}", sessionId);

            // Redis에 senderId가 이미 존재하는지 확인
            if(redisTemplate.hasKey(senderId.toString())) {
                // 이미 존재하는 경우, 중복 연결 차단 메시지 전송 및 senderId가 연결된 소켓 연결 중단
                log.warn("senderId {} 가 이미 존재하여 중복 연결이 차단됨", senderId);
                redisTemplate.delete(senderId.toString());
                messagingTemplate.convertAndSend("/topic/chat/errors/" + senderId,
                    "중복 로그인으로 인해 연결이 거부되었습니다. 해당 채팅방에 다시 접속해주세요.");
            } else {
                // 존재하지 않은 경우, Redis에 key(senderId)와 value(sessionId) 저장
                redisTemplate.opsForValue().set(String.valueOf(senderId),
                    Objects.requireNonNull(sessionId), 1, TimeUnit.DAYS);
                log.info("senderId {} 과 sessionId {} Redis 저장", senderId, sessionId);

                // mongoDB에서 userId와 chatRoomId에 대한 메시지 유무로 처음인지 판단하고 입장 메시지 전송
                List<ChatMessageDocument> messageDocumentList = chatMessageRepository.findByChatRoomIdAndSenderId(chatRoomId, senderId);
                if(messageDocumentList.isEmpty()) {
                    // chatRoomId에서 senderId가 보낸 메시지가 없는 경우, mongoDB 메시지 저장
                    ChatMessageDocument firstMessage = ChatMessageDocument.builder()
                        .chatRoomId(chatRoomId)
                        .senderId(senderId)
                        .connectionType(ConnectionType.ENTER)
                        .chatMessageType(ChatMessageType.TEXT)
                        .messageContent(message)
                        .sentAt(LocalDateTime.now())
                        .build();
                    chatMessageRepository.save(firstMessage);

                    // 처음 채팅방 접속으로 입장 메시지 전송
                    messagingTemplate.convertAndSend("/topic/chat/enter/" + chatRoomId, firstMessage);
                }
            }
        }
    }

    /**
     * stomp 메시지 브로커를 통한 메시지 전송
     */
    public ChatMessageResponseDto sendMessage(ChatMessageRequestDto requestDto) {
        // 메시지 저장 및 전달
        ChatMessageDocument chatMessage = ChatMessageDocument.builder()
            .senderId(requestDto.getSenderId())
            // TODO: 인증 헤더로 전달된 nickname 사용
//            .senderNickname("닉네임1")
            .chatRoomId(requestDto.getChatRoomId())
            .connectionType(ConnectionType.CHAT)
            .chatMessageType(ChatMessageType.TEXT)
            .messageContent(requestDto.getMessageContent())
            .sentAt(LocalDateTime.now())
            .build();
        chatMessageRepository.save(chatMessage);

        return chatMessage.toResponse();
    }

    private void checkRoomIdAndUserId(UUID chatRoomId, Long userId) {
        // 채팅방 유무 검증
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomId(chatRoomId)
            .orElseThrow(() -> new CustomException(CHAT_ROOM_NOT_FOUND));

        // 채팅방 참여자만 해당 메시지 조회 가능
        boolean isParticipant = chatRoom.getChatRoomParticipants().stream()
            .anyMatch(participant -> participant.getUserId().equals(userId));

        if(!isParticipant) {
            throw new CustomException(CHAT_ROOM_USER_ID_NOT_FOUND);
        }
    }

}
