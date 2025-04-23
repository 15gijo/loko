package com.team15gijo.post.infrastructure.kafka.service.v2;

import com.team15gijo.post.infrastructure.kafka.dto.v2.PostElasticsearchRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ElasticsearchKafkaProducerService {

    private final KafkaTemplate<String, PostElasticsearchRequestDto> searchKafkaTemplate;

    public void sendPostCreate(PostElasticsearchRequestDto dto) {
        searchKafkaTemplate.send("POST_SAVE", dto);
    }

}
