package com.team15gijo.chat.application.service.impl.v1;

import com.team15gijo.chat.application.dto.v1.ChatRoomResponseDto;
import com.team15gijo.chat.domain.model.ChatMessageDocument;
import com.team15gijo.chat.domain.model.ChatMessageType;
import com.team15gijo.chat.domain.model.ChatRoom;
import com.team15gijo.chat.domain.model.ChatRoomParticipant;
import com.team15gijo.chat.domain.repository.ChatMessageRepository;
import com.team15gijo.chat.domain.repository.ChatRoomParticipantRepository;
import com.team15gijo.chat.domain.repository.ChatRoomRepository;
import com.team15gijo.chat.presentation.dto.v1.ChatMessageRequestDto;
import com.team15gijo.chat.presentation.dto.v1.ChatMessageResponseDto;
import com.team15gijo.chat.presentation.dto.v1.ChatRoomParticipantRequestDto;
import com.team15gijo.chat.presentation.dto.v1.ChatRoomParticipantResponseDto;
import com.team15gijo.chat.presentation.dto.v1.ChatRoomRequestDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    /**
     * 채팅방 생성(chatRoomType, receiver)에 따른 채팅방 참여자 생성
     * @param requestDto
     * @return ChatRoomResponseDto
     * UserFeignClient 사용자 유효성 검사
     * TODO: userId, nickname 추후 구현
     */
    public ChatRoomResponseDto createChatRoom(ChatRoomRequestDto requestDto) {
        // TODO: User feign client 유효성 검사
        // 현재 상대방 닉네임을 받아 오기 때문에 사용자 feign client로 존재하는지 확인 필요

        // 채팅방 생성 및 저장
        ChatRoom chatRoom = ChatRoom.builder()
            .chatRoomType(requestDto.getChatRoomType())
            .build();
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        // 채팅방 참여자 생성 및 저장
        ChatRoomParticipant participant = ChatRoomParticipant.builder()
            .chatRoom(savedChatRoom)
            // TODO: 인증에서 x-user-id 추출. 현재 임시 값 사용
            .userId(1L)
            .activation(Boolean.TRUE)
            .build();
        chatRoomParticipantRepository.save(participant);

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

        ChatRoomParticipant participant = chatRoomParticipantRepository.findByUserIdAndChatRoom_ChatRoomId(userId, chatRoomId)
            .orElseThrow(() -> new IllegalArgumentException("Participant not found"));

        // 채팅방 및 채팅 메시지 볼 수 없는 비활성화 상태로 변경
        participant.nonActivate();
        chatRoomParticipantRepository.save(participant);
    }

    /**
     * 채팅방에 상대방 참여자 입장(접속)
     * @param request
     * @return
     */
    public ChatRoomParticipantResponseDto addChatParticipant(ChatRoomParticipantRequestDto request) {
        List<ChatRoom> chatRooms = chatRoomParticipantRepository.findChatRoomsByUserId(request.getUserId());
        for(ChatRoom chatRoom : chatRooms) {
            UUID roomId = chatRoom.getChatRoomId();
            boolean participantExists = chatRoomParticipantRepository.findByUserIdAndChatRoom_ChatRoomId(request.getUserId(), roomId)
                .isPresent();
            // 채팅방 참여자 목록에 없으므로 생성
            if(!participantExists) {
                ChatRoomParticipant addParticipant = ChatRoomParticipant.builder()
                    .chatRoom(chatRoom)
                    .userId(request.getUserId())
                    .activation(true)
                    .build();
                chatRoomParticipantRepository.save(addParticipant);
                return addParticipant.toResponse();
            }
        }
        return null;
    }

    /**
     * stomp 메시지 브로커를 통한 메시지 전송
     * TODO: 채팅방 첫 입장 시, 환영 메시지 전송 추가
     * @param requestDto
     * @return
     */
    public ChatMessageResponseDto sendMessage(ChatMessageRequestDto requestDto) {
        log.info("Sending message: {}", requestDto.toString());
        ChatMessageDocument chatMessage = ChatMessageDocument.builder()
            .senderId(requestDto.getSenderId())
            .chatRoomId(requestDto.getChatRoomId())
            .chatMessageType(ChatMessageType.TEXT)
            .messageContent(requestDto.getMessage())
            .sentAt(LocalDateTime.now())
            .build();
        chatMessageRepository.save(chatMessage);

        return chatMessage.toResponse();
    }

}
