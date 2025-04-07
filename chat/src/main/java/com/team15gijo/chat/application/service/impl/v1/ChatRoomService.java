package com.team15gijo.chat.application.service.impl.v1;

import com.team15gijo.chat.application.dto.v1.ChatRoomResponseDto;
import com.team15gijo.chat.domain.model.ChatRoom;
import com.team15gijo.chat.domain.model.ChatRoomParticipant;
import com.team15gijo.chat.domain.model.ChatRoomType;
import com.team15gijo.chat.domain.repository.ChatRoomParticipantRepository;
import com.team15gijo.chat.domain.repository.ChatRoomRepository;
import com.team15gijo.chat.presentation.dto.v1.ChatRoomRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomParticipantRepository chatRoomParticipantRepository;

    /**
     * 1차 mvp 구현은 1:1 채팅
     * @param request
     * @return ChatRoomResponseDto
     * UserFeignClient 사용자 유효성 검사
     */
    public ChatRoomResponseDto createChatRoom(ChatRoomRequest request) {
        // User feign client 유효성 검사

        // 채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
            .chatRoomType(request.getChatRoomType())
            .build();
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        // 채팅방 참여자 생성
        ChatRoomParticipant participant = ChatRoomParticipant.builder()
            .chatRoomId(savedChatRoom.getId())
            // TODO: 인증에서 x-user-id 추출
            .userId(1L)
            .activation(Boolean.TRUE)
            .build();
        chatRoomParticipantRepository.save(participant);

        return savedChatRoom.toResponse();
    }
}
