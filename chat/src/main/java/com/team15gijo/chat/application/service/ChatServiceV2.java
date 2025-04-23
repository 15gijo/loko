package com.team15gijo.chat.application.service;

import com.team15gijo.chat.application.dto.v2.ChatMessageResponseDtoV2;
import com.team15gijo.chat.application.dto.v2.ChatRoomResponseDtoV2;
import com.team15gijo.chat.domain.model.v2.ChatMessageDocumentV2;
import com.team15gijo.chat.domain.model.v2.ChatRoomV2;
import com.team15gijo.chat.presentation.dto.v2.ChatMessageRequestDtoV2;
import com.team15gijo.chat.presentation.dto.v2.ChatRoomRequestDtoV2;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

public interface ChatServiceV2 {

    ChatRoomResponseDtoV2 createChatRoom(
        ChatRoomRequestDtoV2 requestDto,
        Long userId);

    ChatRoomResponseDtoV2 getChatRoom(
        UUID chatRoomId, Long userId);

    Page<ChatRoomV2> getChatRooms(
        Pageable pageable, Long userId);

    boolean exitChatRoom(
        UUID chatRoomId, Long userId);

    Map<String, Object> validateNickname(
        String receiverNickname);

    Map<String, Boolean> validateChatRoomId(
        UUID chatRoomId);

    Map<String, Boolean> validateSenderId(
        UUID chatRoomId, Long senderId);

    Boolean deleteRedisSenderId(
        UUID chatRoomId, String senderId);

    Page<ChatMessageDocumentV2> getMessagesByChatRoomId(
        UUID chatRoomId, Long senderId, Pageable pageable);

    ChatMessageResponseDtoV2 getMessageById(
        UUID chatRoomId, String id, Long userId);

    void connectChatRoom(
        UUID chatRoomId, Long senderId, SimpMessageHeaderAccessor headerAccessor);

    void sendMessage(
        ChatMessageRequestDtoV2 requestDto, SimpMessageHeaderAccessor headerAccessor);

    Page<ChatMessageDocumentV2> searchMessages(
        UUID chatRoomId, LocalDateTime sentAt, String messageContent, Long userId, Pageable pageable);
}
