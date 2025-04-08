package com.team15gijo.chat.presentation.controller.v1;

import com.team15gijo.chat.application.dto.v1.ChatRoomResponseDto;
import com.team15gijo.chat.application.service.impl.v1.ChatRoomService;
import com.team15gijo.chat.presentation.dto.ApiResponse;
import com.team15gijo.chat.presentation.dto.v1.ChatRoomRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/chats")
public class ChatController {

    private final ChatRoomService chatRoomService;

    /**
     * 채팅방 생성(chatRoomType, receiver)에 따른 채티방 참여자 생성
     * TODO: userId, nickname 추후 구현
     */
    @PostMapping("/rooms")
    @ResponseBody
    public ResponseEntity<ApiResponse<ChatRoomResponseDto>> createChatRoom(
        @RequestBody ChatRoomRequest request
    ) {
        ChatRoomResponseDto response = chatRoomService.createChatRoom(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(HttpStatus.OK.value(), response));
    }

    @GetMapping("/rooms/{chatRoomId}")
    public ResponseEntity<ApiResponse<ChatRoomResponseDto>>  getChatRoom(@PathVariable("chatRoomId") UUID chatRoomId) {
        ChatRoomResponseDto response = chatRoomService.getChatRoom(chatRoomId);
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(HttpStatus.OK.value(), response));
    }
}
