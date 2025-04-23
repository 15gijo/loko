package com.team15gijo.ai.infrastructure.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team15gijo.ai.application.config.GeminiApiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class GeminiApiClient {
    private final GeminiApiProperties properties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Gemini API에 prompt를 보내고, 생성된 텍스트를 반환합니다.
     */
    public String requestAiMessage(String prompt) {
        String url = properties.getApiUrl() + "?key=" + properties.getApiKey();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 JSON 생성
        JsonNode part = objectMapper.createObjectNode().put("text", prompt);
        JsonNode promptObj = objectMapper.createObjectNode()
                .set("parts", objectMapper.createArrayNode().add(part));
        JsonNode body = objectMapper.createObjectNode()
                .set("contents", objectMapper.createArrayNode().add(promptObj));

        HttpEntity<JsonNode> request = new HttpEntity<>(body, headers);
        ResponseEntity<JsonNode> resp = restTemplate.postForEntity(url, request, JsonNode.class);

        if (resp.getStatusCode() != HttpStatus.OK || resp.getBody() == null) {
            throw new RuntimeException("Gemini API 호출 실패: " + resp.getStatusCode());
        }

        JsonNode candidates = resp.getBody().path("candidates");
        if (candidates.isArray() && candidates.size() > 0) {
            JsonNode textNode = candidates.get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text");
            return textNode.asText();
        }

        throw new RuntimeException("Gemini API 응답에 텍스트가 없습니다.");
    }
}
