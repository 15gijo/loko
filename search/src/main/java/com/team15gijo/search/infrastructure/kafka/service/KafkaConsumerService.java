package com.team15gijo.search.infrastructure.kafka.service;

import com.team15gijo.search.application.service.v2.ElasticsearchService;
import com.team15gijo.search.infrastructure.kafka.dto.PostElasticsearchRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final ElasticsearchService elasticsearchService;

    @KafkaListener(topics = "POST_SAVE", groupId = "search-service", containerFactory = "postKafkaListenerContainerFactory")
    @Transactional
    public void postConsumer(PostElasticsearchRequestDto dto) {
        log.info("📨 ElasticSearch에 게시글 저장을 위한 Kafka 메시지 : {}", dto);
        elasticsearchService.createElasticPost(dto);
    }
}
