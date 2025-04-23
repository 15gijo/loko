package com.team15gijo.ai.presentation.controller;

import com.team15gijo.ai.application.service.ContentModerationService;
import com.team15gijo.ai.presentation.dto.ModerationRequestDto;
import com.team15gijo.ai.presentation.dto.ModerationResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/moderation")
@RequiredArgsConstructor
public class ModerationController {
    private final ContentModerationService moderationService;

    @PostMapping
    public ResponseEntity<ModerationResponseDto> moderate(@RequestBody ModerationRequestDto request) {
        boolean off = moderationService.isOffensive(request.getCommentContent());
        return ResponseEntity.ok(new ModerationResponseDto(off));
    }
}
