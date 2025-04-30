package com.team15gijo.follow.infrastructure.kafka.producer.v2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team15gijo.common.exception.CustomException;
import com.team15gijo.follow.infrastructure.exception.FollowInfraExceptionCode;
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

    private final KafkaTemplate<String, FollowEventDto> internalKafkaTemplate;
    private final KafkaTemplate<String, String> externalKafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String FOLLOW_CREATED_TOPIC = "follow.created";
    private static final String FOLLOW_DELETED_TOPIC = "follow.deleted";
    private static final String USER_FOLLOW_COUNT_CHANGED_TOPIC = "user.follow.count.changed";

    @Override
    public void sendFollowCreated(Long followerId, Long followeeId) {
        FollowEventDto followEventDto = FollowEventDto.of(
                followerId,
                followeeId,
                FollowType.FOLLOW,
                LocalDateTime.now()
        );
        sendInternalMessage(FOLLOW_CREATED_TOPIC, String.valueOf(followerId), followEventDto);
        sendExternalMessage(USER_FOLLOW_COUNT_CHANGED_TOPIC, String.valueOf(followerId), followEventDto);
        log.info("팔로우 생성 로직 완료: followerId = {}, followeeId = {}", followerId, followeeId);
    }

    @Override
    public void sendFollowDeleted(Long followerId, Long followeeId) {
        FollowEventDto followEventDto = FollowEventDto.of(
                followerId,
                followeeId,
                FollowType.UNFOLLOW,
                LocalDateTime.now()
        );
        sendInternalMessage(FOLLOW_DELETED_TOPIC, String.valueOf(followerId), followEventDto);
        sendExternalMessage(USER_FOLLOW_COUNT_CHANGED_TOPIC, String.valueOf(followerId), followEventDto);
        log.info("언팔로우 생성 로직 완료: followerId = {}, followeeId = {}", followerId, followeeId);
    }

    private void sendInternalMessage(String topic, String key, FollowEventDto followEventDto) {
        internalKafkaTemplate.send(topic, key, followEventDto);
        log.info("[kafka-producer-follow] 내부 카프카 메시지 전송 완료: topic={}, key={}, payload={}", topic,
                key, followEventDto);
    }

    private void sendExternalMessage(String topic, String key, FollowEventDto followEventDto) {
        try {
            String message = objectMapper.writeValueAsString(followEventDto);
            externalKafkaTemplate.send(topic, key, message);
            log.info("[kafka-producer-follow] 외부 카프카 메시지 전송 완료: topic={}, key={}, payload={}",
                    topic, key, message);
        } catch (JsonProcessingException e) {
            log.error("Kafka 메시지 직렬화 실패: {}", followEventDto, e);
            throw new CustomException(FollowInfraExceptionCode.KAFKA_JSON_SERIALIZATION_FAILED, e);
        }
    }

}
