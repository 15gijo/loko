package com.team15gijo.ai.application.service;

import com.team15gijo.ai.infrastructure.client.GeminiApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HashtagRecommendationService {
    private final GeminiApiClient geminiApiClient;

    /**
     * 게시글 내용을 바탕으로 한국어 해시태그 5개를 추천하여 반환합니다.
     */
    public List<String> recommendHashtags(String postContent) {
        String prompt = String.format(
                "아래 게시글 내용을 바탕으로, 한국어 해시태그 3개를 “#태그” 형태로, 쉼표로 구분하여 출력해줘:\n\n%s",
                postContent
        );

        String aiResponse = geminiApiClient.requestAiMessage(prompt);

        return Arrays.stream(aiResponse.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}