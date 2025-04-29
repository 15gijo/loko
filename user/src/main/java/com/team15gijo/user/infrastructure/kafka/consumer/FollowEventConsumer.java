package com.team15gijo.user.infrastructure.kafka.consumer;

import com.team15gijo.user.application.service.UserApplicationService;
import com.team15gijo.user.infrastructure.kafka.dto.FollowEventDto;
import com.team15gijo.user.infrastructure.kafka.dto.FollowEventDto.FollowType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FollowEventConsumer {

    private final UserApplicationService userApplicationService;

    @KafkaListener(topics = {"follow.created", "follow.deleted"},
            groupId = "follow-consumer-group")
    public void consumeFollowEvent(FollowEventDto followEventDto) {
        log.info("[Kafka-Consumer] FollowEventDto 수신: {}", followEventDto);

        if (followEventDto.followType() == FollowType.FOLLOW) {
            userApplicationService.increaseFollowCount(followEventDto.followerId(),
                    followEventDto.followeeId());
        } else if (followEventDto.followType() == FollowType.UNFOLLOW) {
            userApplicationService.decreaseFollowCount(followEventDto.followerId(),
                    followEventDto.followeeId());
        } else {
            log.warn("[Kafka-Consumer] 알 수 없는 FollowType 수신: {}", followEventDto.followType());
        }
    }
}
