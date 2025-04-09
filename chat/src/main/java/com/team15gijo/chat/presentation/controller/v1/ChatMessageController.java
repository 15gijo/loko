package com.team15gijo.chat.presentation.controller.v1;

import com.team15gijo.chat.application.dto.v1.ChatRoomResponseDto;
import com.team15gijo.chat.application.service.impl.v1.ChatMessageService;
import com.team15gijo.chat.domain.model.ChatRoom;
import com.team15gijo.chat.presentation.dto.v1.ChatMessageRequestDto;
import com.team15gijo.chat.presentation.dto.v1.ChatMessageResponseDto;
import com.team15gijo.chat.presentation.dto.v1.ChatRoomParticipantRequestDto;
import com.team15gijo.chat.presentation.dto.v1.ChatRoomParticipantResponseDto;
import com.team15gijo.chat.presentation.dto.v1.ChatRoomRequestDto;
import com.team15gijo.common.dto.ApiResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/chats")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    /**
     * 채팅방 생성(chatRoomType, receiver)에 따른 채팅방 참여자 생성
     *TODO: userId, nickname 추후 구현
     * 채팅방이 INDIVIDUAL 타입에서는 3명 이상 메시지 전송 불가로 제한 예정
     */
    @PostMapping("/rooms")
    @ResponseBody
    public ResponseEntity<ApiResponse<ChatRoomResponseDto>> createChatRoom(
        @RequestBody ChatRoomRequestDto requestDto
    ) {
        ChatRoomResponseDto response = chatMessageService.createChatRoom(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("채팅방 생성되었습니다.", response));
    }

    /**
     * 채팅방 단일 조회
     */
    @GetMapping("/rooms/{chatRoomId}")
    public ResponseEntity<ApiResponse<ChatRoomResponseDto>>  getChatRoom(@PathVariable("chatRoomId") UUID chatRoomId) {
        ChatRoomResponseDto response = chatMessageService.getChatRoom(chatRoomId);
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success("채팅방 조회되었습니다.", response));
    }

    /**
     * 채팅방 전체 조회
     */
    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse<Page<ChatRoom>>> getChatRooms(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "chatRoomId") String sortField,
        @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        Page<ChatRoom> chatRooms = chatMessageService.getChatRooms(pageable);
        return ResponseEntity.ok(ApiResponse.success("채팅방 목록이 조회되었습니다.", chatRooms));
    }

    /**
     * 채팅방 삭제(퇴장) -> 1명의 사용자 채팅방 퇴장(채팅방 참여자 비활성화로 변경)
     */
    @DeleteMapping("/rooms/{chatRoomId}/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteChatRoom(
        @PathVariable("chatRoomId") UUID chatRoomId,
        @PathVariable("userId") Long userId) {
        chatMessageService.deleteChatRoom(chatRoomId, userId);
        return ResponseEntity.ok(ApiResponse.success("채팅방이 삭제되었습니다."));
    }

    /**
     * 채팅방에 상대방 참여자 입장(접속)
     * TODO: 채팅방에 상대방 참여자 입장(접속) -> 메시지 브로커로 연결되야 함
     */
    @PostMapping("/rooms/participants")
    @ResponseBody
    public ResponseEntity<ApiResponse<ChatRoomParticipantResponseDto>> addChatParticipant(
        @RequestBody ChatRoomParticipantRequestDto request
    ) {
        ChatRoomParticipantResponseDto response = chatMessageService.addChatParticipant(request);
        return ResponseEntity.ok(ApiResponse.success("채팅방 참여자가 추가되었습니다.", response));
    }

    /**
     * "/ws-stomp" 경로로 소켓 연결하여
     * "/topic"을 구독하는 서버에서 실시간 메시지 송수신 가능
     * "/app" 시작하는 경로 stomp 메시지 전송하면 @MessageMapping 으로 연결
     * @param requestDto
     * @return
     */
    @MessageMapping("/chat/{chatRoomId}")
    @SendTo("/topic/chat/{chatRoomId}")
    public ChatMessageResponseDto sendMessage(@RequestBody ChatMessageRequestDto requestDto) {
        log.info("Sending message: {}", requestDto.toString());
        return chatMessageService.sendMessage(requestDto);
    }
}
