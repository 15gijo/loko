package com.team15gijo.post.infrastructure.kafka.service.v1;

import com.team15gijo.post.infrastructure.kafka.dto.v1.FeedEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProducerService {

    private final KafkaTemplate<String, FeedEventDto> postCreadtedkafkaTemplate;


    public void sendMessage(String topic , String key, FeedEventDto event) {
        postCreadtedkafkaTemplate.send(topic, key, event);
    }
}
