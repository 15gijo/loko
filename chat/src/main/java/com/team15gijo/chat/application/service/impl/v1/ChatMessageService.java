package com.team15gijo.chat.application.service.impl.v1;

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
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * 1차 MVP 1:1 채팅만 구현
     * 채팅방 생성(chatRoomType, receiver)에 따른 채팅방 참여자 생성
     * @param requestDto
     * @return ChatRoomResponseDto
     * UserFeignClient 사용자 유효성 검사
     * TODO: userId, nickname 추후 구현
     */
    public ChatRoomResponseDto createChatRoom(ChatRoomRequestDto requestDto) {
        //TODO: User feign client 유효성 검사 구현 후, 연결 확인
        // 채팅방 생성 시, 상대방 계정 조회(nickname 으로 존재유무 판단)
        // 상대방과 본인 userId를 모두 추출하여 채팅방 참여자 생성하기!
        String receiverNickname = requestDto.getReceiverNickname();
//        Long getUserIdByNickname = feignClientService.fetchUserIdByNickname(receiverNickname);
//        if(getUserIdByNickname == null) {
//            throw new NullPointerException("Nickname " + receiverNickname + " does not exist");
//        }

        // 채팅방 생성하는 당사자의 참여자 생성 및 저장
        ChatRoomParticipant addParticipants = ChatRoomParticipant.builder()
            // TODO: 인증에서 x-user-id 추출. 현재 임시 값 사용
            .userId(1L)
            .activation(Boolean.TRUE)
            .build();
        chatRoomParticipantRepository.save(addParticipants);

        Set<ChatRoomParticipant> participantsSet = new HashSet<>();
        participantsSet.add(addParticipants);

//        // 초대받은 참여자의 채팅방 참여자 생성 및 저장
//        ChatRoomParticipant invitedParticipant = ChatRoomParticipant.builder()
//            .userId(getUserIdByNickname)
//            .activation(Boolean.FALSE)
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
            throw new IllegalArgumentException("1:1 채팅은 2명만 참여할 수 있습니다.");
        }

        return savedChatRoom.toResponse();
    }

    /**
     * 채팅방 단일 조회
     * @param chatRoomId
     * @return
     */
    @Transactional(readOnly = true)
    public ChatRoomResponseDto getChatRoom(UUID chatRoomId) {
    ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).
        orElseThrow(() -> new IllegalArgumentException("Chat room not found"));

    return chatRoom.toResponse();
    }

    /**
     * 채팅방 전체 조회
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public Page<ChatRoom> getChatRooms(Pageable pageable) {
        return chatRoomRepository.findAll(pageable);
    }

    /**
     * 채팅방 삭제(퇴장) -> 1명의 사용자 채팅방 퇴장(채팅방 참여자 비활성화로 변경)
     * @param chatRoomId
     * @param userId
     */
    public void deleteChatRoom(UUID chatRoomId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomId(chatRoomId)
            .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));

        Set<ChatRoomParticipant> updatedParticipant = chatRoom.getChatRoomParticipants().stream()
            .map(participant -> {
                if(participant.getUserId().equals(userId)) {
                    // 기존 participant 제거 후
                    chatRoom.getChatRoomParticipants().remove(participant);
                    // 채팅방 및 채팅 메시지 볼 수 없는 비활성화 상태로 변경 및 저장
                    participant.nonActivate();
                    chatRoomParticipantRepository.save(participant);
                    // 변경된 participant Set 추가
                    chatRoom.getChatRoomParticipants().add(participant);
                }
                return participant;
            })
            .collect(Collectors.toSet());

        chatRoomRepository.save(chatRoom);
    }

    /**
     * 채팅방에 상대방 참여자 입장(접속) -> 채팅방 생성과 동시에 참여자 전원 채팅방참여자 생성 구현함
     * 그러나, User feign client로 상대방 닉네임으로 userId 조회하여 참여자 등록 전까지
     * TODO: 현재 참여자 등록으로 테스트 진행 예정 -> 최종 구현 이후 삭제 예정
     * @param request(userId, chatRoomId)
     * @return
     */
    public ChatRoomParticipantResponseDto addChatParticipant(ChatRoomParticipantRequestDto request) {
        ChatRoom findChatRoom = chatRoomRepository.findByChatRoomId(request.getChatRoomId())
            .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));

        ChatRoomParticipant participant = ChatRoomParticipant.builder()
            .userId(request.getUserId())
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
    public Map<String, Boolean> validateSenderId(UUID chatRoomId, Long senderId) {
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomId(chatRoomId)
            .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));

        Boolean participantExists = chatRoom.getChatRoomParticipants().stream()
            .anyMatch(participant -> participant.getUserId().equals(senderId));
        log.info("participantExists {}", participantExists);

        Map<String, Boolean> response = new HashMap<>();
        response.put("valid", participantExists);
        return response;
    }
    /**
     * "/ws-stomp" 경로로 소켓 연결 시, 채팅방에 참여한 user가 Redis 캐시 조회하여 있는 경우 소켓 연결 중단
     * 서버 리셋 후, 다시 소켓 연결하면 재접속 가능
     * 웹 소켓 연결 시, redis senderId(key)-sessionId(value) 캐시 저장
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
                redisTemplate.opsForValue().set(String.valueOf(senderId), sessionId);
                log.info("senderId {} 과 sessionId {} Redis 저장", senderId, sessionId);

                // mongoDB에서 userId와 chatRoomId에 대한 메시지 유무로 처음인지 판단하고 입장 메시지 전송
                List<ChatMessageDocument> messageDocumentList = chatMessageRepository.findByChatRoomIdAndSenderId(chatRoomId, senderId);
                if(messageDocumentList.isEmpty()) {
                    // chatRoomId에서 senderId가 보낸 메시지가 없는 경우, mongoDB 메시지 저장
                    ChatMessageDocument firstMessage = ChatMessageDocument.builder()
                        .chatRoomId(chatRoomId)
                        .senderId(senderId)
                        .connectionType(ConnectionType.ENTER)
                        .messageContent(message)
                        .sentAt(LocalDateTime.now())
                        .build();
                    chatMessageRepository.save(firstMessage);

                    // 처음 채팅방 접속으로 입장 메시지 전송
                    messagingTemplate.convertAndSend("/topic/chat/enter/" + chatRoomId, message);
                }
            }
        }
    }


    /**
     * stomp 메시지 브로커를 통한 메시지 전송
     * TODO: 채팅방 첫 입장 시, 환영 메시지 전송 추가
     * @param requestDto
     * @return
     */
    public ChatMessageResponseDto sendMessage(ChatMessageRequestDto requestDto) {
        // 메시지 저장 및 전달
        ChatMessageDocument chatMessage = ChatMessageDocument.builder()
            .senderId(requestDto.getSenderId())
            // TODO: 인증 헤더로 전달된 nickname 사용
//            .senderNickname("닉네임1")
            .chatRoomId(requestDto.getChatRoomId())
            .chatMessageType(ChatMessageType.TEXT)
            .messageContent(requestDto.getMessage())
            .sentAt(LocalDateTime.now())
            .build();
        chatMessageRepository.save(chatMessage);

        return chatMessage.toResponse();
    }

}
