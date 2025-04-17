package com.team15gijo.ai.presentation.controller;

import com.team15gijo.ai.application.service.HashtagRecommendationService;
import com.team15gijo.ai.presentation.dto.HashtagRequestDto;
import com.team15gijo.ai.presentation.dto.HashtagResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/hashtags")
@RequiredArgsConstructor
public class HashtagController {
    private final HashtagRecommendationService service;

    @PostMapping
    public ResponseEntity<HashtagResponseDto> recommend(@RequestBody HashtagRequestDto request) {
        List<String> tags = service.recommendHashtags(request.getPostContent());
        return ResponseEntity.ok(new HashtagResponseDto(tags));
    }
}
