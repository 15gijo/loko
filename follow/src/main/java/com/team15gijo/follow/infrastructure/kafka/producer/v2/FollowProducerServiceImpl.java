package com.team15gijo.follow.infrastructure.kafka.producer.v2;

import com.team15gijo.follow.infrastructure.kafka.dto.v2.FollowEventDto;
import com.team15gijo.follow.infrastructure.kafka.dto.v2.FollowEventDto.FollowType;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FollowProducerServiceImpl implements FollowProducerService {

    private final KafkaTemplate<String, FollowEventDto> kafkaTemplate;

    private static final String FOLLOW_CREATED_TOPIC = "follow.created";
    private static final String FOLLOW_DELETED_TOPIC = "follow.deleted";

    @Override
    public void sendFollowCreated(Long followerId, Long followeeId) {
        FollowEventDto followEventDto = FollowEventDto.of(
                followerId,
                followeeId,
                FollowType.FOLLOW,
                LocalDateTime.now()
        );
        kafkaTemplate.send(FOLLOW_CREATED_TOPIC, String.valueOf(followerId), followEventDto);
        log.info("팔로우 생성 토픽 전송: followerId = {}, followeeId = {}", followerId, followeeId);
    }

    @Override
    public void sendFollowDeleted(Long followerId, Long followeeId) {
        FollowEventDto followEventDto = FollowEventDto.of(
                followerId,
                followeeId,
                FollowType.UNFOLLOW,
                LocalDateTime.now()
        );
        kafkaTemplate.send(FOLLOW_DELETED_TOPIC, String.valueOf(followerId), followEventDto);
        log.info("언팔로우 생성 토픽 전송: followerId = {}, followeeId = {}", followerId, followeeId);
    }
}
