package com.team15gijo.ai.application.service;

import com.team15gijo.ai.infrastructure.client.GeminiApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageRestrictionService {
    private final GeminiApiClient geminiApiClient;

    /**
     * 채팅 메시지 내용을 토대로 ai 사용하여 유해/불법적인 내용 제한 및 메시지 삭제 처리
     */
    public boolean restrictMessage(String messageContent) {
        log.info("[ChatMessageRestrictionService] restrictMessage 메서드 실행 - messageContent={}", messageContent);
        String prompt = String.format(
            "다음 채팅 메시지에 유해하거나 불쾌감을 주는 단어나 불법적인 단어 또는 욕설이 포함되어 있는지 판단하여, 포함되어 있다면 \"true\" 또는 \"false\"만 응답해주세요.\n\n%s",
            messageContent
        );

        String aiResponse = null;
        try{
            aiResponse = geminiApiClient.requestAiMessage(prompt);
            log.info("[ChatMessageRestrictionService] Gemini API 응답:{}", aiResponse);

            // Gemini API 응답 처리
            if(aiResponse != null) {
                String response = aiResponse.trim().toLowerCase();
                log.info("[ChatMessageRestrictionService] restrictMessage 메서드 종료 - isHarmful={}", response.equals("true"));
                return response.equals("true");
            }
            log.info("[ChatMessageRestrictionService] restrictMessage 메서드 종료 - false");
            return false;
        } catch (RuntimeException e) {
            log.error("[ChatMessageRestrictionService] Gemini API 호출 실패:{}", e.getMessage());
            // API 호출 실패는 false 처리
            return false;
        }
    }
}
