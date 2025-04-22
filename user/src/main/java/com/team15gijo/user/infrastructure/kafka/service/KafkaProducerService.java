package com.team15gijo.user.infrastructure.kafka.service;

import com.team15gijo.user.infrastructure.kafka.dto.UserElasticsearchRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, UserElasticsearchRequestDto> searchKafkaTemplate;

    public void sendUserCreate(UserElasticsearchRequestDto dto) {
        searchKafkaTemplate.send("USER_SAVE", dto);
    }

}
