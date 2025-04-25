package com.team15gijo.chat.infrastructure.slack.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team15gijo.chat.domain.exception.ChatDomainExceptionCode;
import com.team15gijo.chat.infrastructure.kafka.dto.ChatMessageEventDto;
import com.team15gijo.common.exception.CustomException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class SlackNotificationService {

    @Value("${slack.web-hook-url}")
    private String slackWebhookUrl;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    /**
     * kafka Listener에서 호출하여 slack 채널에 에러 메시지 전송
     */
    public void handleDltMessage(ChatMessageEventDto chatMessageEventDto, String topic) {
        log.error("[SlackNotificationService] handleDltMessage 메서드 실행");
        log.error("[SlackNotificationService] topic: {}", topic);

        try {
            log.error("[SlackNotificationService] DLT로 이동된 메시지: {}", chatMessageEventDto.getMessageContent());

            // 슬랙 알림 메시지 전송
            sendSlackNotification(chatMessageEventDto, topic);
        } catch (Exception e) {
            log.error("[SlackNotificationService] DLT 메시지 처리 중 오류 발생:{}", e.getMessage());
            throw new CustomException(ChatDomainExceptionCode.KAFKA_DLT_PROCESS_ERROR);
        }
        log.error("[SlackNotificationService] handleDltMessage 메서드 종료");
    }

    // 슬랙 알림 메시지 전송
    private void sendSlackNotification(ChatMessageEventDto chatMessageEventDto, String topic) {
        log.error("[SlackNotificationService] sendSlackNotification 메서드 실행");
        try {
            // 슬랙 메시지 전송 template 변환
            List<Map<String, Object>> blocks = blockTemplate(chatMessageEventDto, topic);

            Map<String, Object> payload = new HashMap<>();
            payload.put("blocks", blocks);

            // 슬랙 웹후크 로 메시지 전송
            String jsonPayload = objectMapper.writeValueAsString(payload);
            log.info("jsonPayload: {}", jsonPayload);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

            // RestTemplate으로 POST 요청 전송
            restTemplate.postForEntity(slackWebhookUrl, request, String.class);

            log.info("[SlackNotificationService] 슬랙 알림 전송 성공 - chatMessageEventDto: {}", chatMessageEventDto);
        } catch (JsonProcessingException e) {
            log.error("[SlackNotificationService] 슬랙 알림 전송 JSO 파싱 에러 - ErrorMessage:{}", e.getMessage());
            throw new CustomException(ChatDomainExceptionCode.SEND_NOTIFICATION_TO_SLACK_FOR_JSON);
        } catch (RestClientException e) {
            log.error("[SlackNotificationService] 슬랙 알림 전송 실패 - ErrorMessage:{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("[SlackNotificationService] 그 외 슬랙 알림 전송 실패 - ErrorMessage:{}", e.getMessage());
            throw new CustomException(ChatDomainExceptionCode.SEND_NOTIFICATION_TO_SLACK_ERROR);
        }
        log.error("[SlackNotificationService] sendSlackNotification 메서드 종료");
    }

    // 슬랙 메시지 전송 template 변환
    private List<Map<String, Object>> blockTemplate(ChatMessageEventDto chatMessageEventDto, String topic) {
        List<Map<String, Object>> blocks = new ArrayList<>();

        // 제목 블록
        blocks.add(Map.of(
            "type", "header",
            "text", Map.of(
                "type", "plain_text",
                "text", "🚨DLT(topics=`" + topic + "`) 에러 발생 🚨",
                "emoji", true
            )
        ));

        // 구분선
        blocks.add(Map.of("type", "divider"));

        // 내용 블록 : 슬랙 메시지 전송 내용
        // 각 정보를 별도의 section 블록으로 추가
        blocks.add(Map.of(
            "type", "section",
            "text", Map.of(
                "type", "mrkdwn",
                "text", "*채팅방 ID:* `" + chatMessageEventDto.getChatRoomId() + "`"
            )
        ));
        blocks.add(Map.of(
            "type", "section",
            "text", Map.of(
                "type", "mrkdwn",
                "text", "*보낸 사람:* `" + chatMessageEventDto.getSenderNickname() + "(" + chatMessageEventDto.getSenderId() + ")`"
            )
        ));
        blocks.add(Map.of(
            "type", "section",
            "text", Map.of(
                "type", "mrkdwn",
                "text", "*받는 사람:* `" + chatMessageEventDto.getReceiverNickname() + "(" + chatMessageEventDto.getReceiverId() + ")`"
            )
        ));
        blocks.add(Map.of(
            "type", "section",
            "text", Map.of(
                "type", "mrkdwn",
                "text", "*메시지 내용:* ```" + chatMessageEventDto.getMessageContent() + "```"
            )
        ));
        blocks.add(Map.of(
            "type", "section",
            "text", Map.of(
                "type", "mrkdwn",
                "text", "*전송 시간:* `" + chatMessageEventDto.getSentAt() + "`"
            )
        ));

        return blocks;
    }
}
