package com.team15gijo.ai.application.service;


import com.team15gijo.ai.infrastructure.client.GeminiApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContentModerationService {
    private final GeminiApiClient geminiApiClient;

    /**
     * Gemini API에 공격적 표현 포함 여부(true/false) 판단 요청
     */
    public boolean isOffensive(String content) {
        String prompt = String.format(
                "아래 텍스트가 욕설/혐오 발언 등 공격적 표현을 포함하는지, \"true\" 또는 \"false\" 만 출력해줘:\n\n\"%s\"",
                content
        );
        return geminiApiClient
                .requestAiMessage(prompt)
                .trim()
                .toLowerCase()
                .startsWith("true");
    }
}

