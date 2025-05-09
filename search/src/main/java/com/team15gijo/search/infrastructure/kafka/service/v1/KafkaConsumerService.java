package com.team15gijo.search.infrastructure.kafka.service.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team15gijo.common.exception.CustomException;
import com.team15gijo.search.application.service.v2.ElasticsearchService;
import com.team15gijo.search.domain.exception.SearchDomainExceptionCode;
import com.team15gijo.search.domain.model.DlqEntity;
import com.team15gijo.search.domain.repository.DlqRepository;
import com.team15gijo.search.infrastructure.kafka.dto.v1.PostElasticsearchRequestDto;
import com.team15gijo.search.infrastructure.kafka.dto.v1.UserElasticsearchRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final ElasticsearchService elasticsearchService;
    private final ObjectMapper objectMapper;
    private final DlqRepository dlqRepository;

    @KafkaListener(topics = "POST_SAVE", groupId = "search-service", containerFactory = "postKafkaListenerContainerFactory")
    @Transactional
    @RetryableTopic(attempts = "3",
            backoff = @Backoff(delay = 1000, maxDelay = 3000, random = true), // 1~3초 랜덤 재시도
            dltStrategy = DltStrategy.FAIL_ON_ERROR, // 재시도 실패 후 DLQ 이동
            dltTopicSuffix = "-dlt", // DLQ 토픽 접미사
            exclude = { IllegalArgumentException.class, NullPointerException.class }
    )
    public void postConsumer(PostElasticsearchRequestDto dto) {
        try {
            log.info("📨 ElasticSearch에 게시글 저장을 위한 Kafka 메시지 : {}", dto);
            elasticsearchService.createElasticPost(dto);
        } catch (Exception e) {
            log.error("❌ 게시글 생성 Kafka 메시지 처리 중 예외 발생", e);
            throw e; // 반드시 다시 던져야 재시도 + DLQ 작동함
        }
    }


    @KafkaListener(topics = "USER_SAVE", groupId = "search-service", containerFactory = "userKafkaListenerContainerFactory")
    @Transactional
    @RetryableTopic(attempts = "3",
            backoff = @Backoff(delay = 1000, maxDelay = 3000, random = true), // 1~3초 랜덤 재시도
            dltStrategy = DltStrategy.FAIL_ON_ERROR, // 재시도 실패 후 DLQ 이동
            dltTopicSuffix = "-dlt", // DLQ 토픽 접미사
            exclude = { IllegalArgumentException.class, NullPointerException.class }
    )
    public void userConsumer(UserElasticsearchRequestDto dto) {
        try {
            log.info("📨 ElasticSearch에 사용자 저장을 위한 Kafka 메시지 : {}", dto);
            elasticsearchService.createElasticUser(dto);
        } catch (Exception e) {
            log.error("❌ 유저 생성 Kafka 메시지 처리 중 예외 발생", e);
            throw e; // 반드시 다시 던져야 재시도 + DLQ 작동함
        }
    }


    @KafkaListener(topics = "USER_SAVE-dlt", groupId = "search-service", containerFactory = "userKafkaListenerContainerFactory")
    public void handleUserEventDlt(UserElasticsearchRequestDto dto) {
        log.error("🔥 DLQ로 이동된 메시지 수신: {}", dto);
        try {
            String payload = objectMapper.writeValueAsString(dto);

            DlqEntity dlq = DlqEntity.builder()
                    .type("USER_SAVE")
                    .payload(payload)
                    .errorMessage("유저 인덱싱 실패")
                    .resolved(false)
                    .build();

            dlqRepository.save(dlq);
        } catch (JsonProcessingException e) {
            log.error("❌ 유저 DLQ 저장 중 직렬화 실패", e);
            throw new CustomException(SearchDomainExceptionCode.DLT_SAVE_FAIL);
        }
    }


    @KafkaListener(topics = "POST_SAVE-dlt", groupId = "search-service", containerFactory = "postKafkaListenerContainerFactory")
    public void handlePostEventDlt(PostElasticsearchRequestDto dto) {
        log.error("🔥 DLQ로 이동된 메시지 수신: {}", dto);
        // Slack 알림 보내거나 Kibana/DB 저장 등 추가
    }
}
