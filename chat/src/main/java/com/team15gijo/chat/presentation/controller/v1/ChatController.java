package com.team15gijo.chat.presentation.controller.v1;

import com.team15gijo.chat.application.dto.v1.ChatRoomResponseDto;
import com.team15gijo.chat.application.service.impl.v1.ChatRoomService;
import com.team15gijo.chat.presentation.dto.v1.ChatRoomRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chats")
public class ChatController {

    private final ChatRoomService chatRoomService;

    /**
     * 채팅방 생성(chatRoomType, receiver)
     * TODO: userId, nickname 추후 구현
     */
    @PostMapping("/rooms")
    public ResponseEntity<ChatRoomResponseDto> createChatRoom(
        @RequestBody ChatRoomRequest request
    ) {
        ChatRoomResponseDto response = chatRoomService.createChatRoom(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
