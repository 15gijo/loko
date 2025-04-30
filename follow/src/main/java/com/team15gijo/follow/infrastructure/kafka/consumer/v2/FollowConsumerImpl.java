package com.team15gijo.follow.infrastructure.kafka.consumer.v2;

import com.team15gijo.follow.infrastructure.kafka.dto.v2.FollowEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FollowConsumerImpl implements FollowConsumer {

    private final FollowConsumerService followConsumerService;

    @Override
    @KafkaListener(
            topics = "follow.created",
            groupId = "follow-service-group",
            containerFactory = "followKafkaListenerContainerFactory")
    public void consumeFollowCreated(FollowEventDto followEventDto) {
        log.info("[Kafka-consumer] follower.created 수신: {}", followEventDto);
        followConsumerService.handleFollowCreated(followEventDto);
    }

    @Override
    @KafkaListener(
            topics = "follow.deleted",
            groupId = "follow-service-group",
            containerFactory = "followKafkaListenerContainerFactory")
    public void consumeFollowDeleted(FollowEventDto followEventDto) {
        log.info("[Kafka-consumer] follower.deleted 수신: {}", followEventDto);
        followConsumerService.handleFollowDeleted(followEventDto);
    }
}
