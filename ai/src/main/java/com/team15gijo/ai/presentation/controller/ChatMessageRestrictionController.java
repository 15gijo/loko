package com.team15gijo.ai.presentation.controller;

import com.team15gijo.ai.application.service.ChatMessageRestrictionService;
import com.team15gijo.ai.presentation.dto.MessageFilteringResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v2/ai")
@RequiredArgsConstructor
public class ChatMessageRestrictionController {
    private final ChatMessageRestrictionService service;

    @PostMapping("/chats/message/restrict")
    public ResponseEntity<MessageFilteringResponseDto> restrict(@RequestBody String messageContent) {
        log.info("[ChatMessageRestrictionController] restrict 메서드 실행");

        boolean isHarmful = service.restrictMessage(messageContent);
        log.info("[ChatMessageRestrictionController] restrict 메서드 종료 - isHarmful={}", isHarmful);

        MessageFilteringResponseDto responseData = MessageFilteringResponseDto.from(isHarmful, messageContent);
        return ResponseEntity.ok(responseData);
    }
}
