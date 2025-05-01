package com.team15gijo.user.infrastructure.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team15gijo.common.exception.CustomException;
import com.team15gijo.user.application.service.UserApplicationService;
import com.team15gijo.user.infrastructure.exception.UserInfraExceptionCode;
import com.team15gijo.user.infrastructure.kafka.dto.FollowEventDto;
import com.team15gijo.user.infrastructure.kafka.dto.FollowEventDto.FollowType;
import com.team15gijo.user.infrastructure.scheduler.support.FollowCountDeltaDto;
import com.team15gijo.user.infrastructure.scheduler.support.FollowCountDeltaDto.TargetType;
import com.team15gijo.user.infrastructure.scheduler.support.FollowCountEventQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FollowEventConsumer {

    private final UserApplicationService userApplicationService;
    private final ObjectMapper objectMapper;
    private final FollowCountEventQueue followCountEventQueue;

    //    @KafkaListener(
//            topics = "user.follow.count.changed",
//            groupId = "user-service-group",
//            containerFactory = "UserKafkaListenerContainerFactory"
//    )
//    public void consumeFollowEvent(String message) {
//        log.info("[Kafka-Consumer-User] FollowEventDto.toString 수신: {}", message);
//
//        FollowEventDto followEventDto = deserializeMessage(message);
//
//        try {
//            log.info("[Kafka-Consumer-User] FOLLOW EVENT DTO: {}", followEventDto);
//            if (followEventDto.getFollowType() == FollowType.FOLLOW) {
//                userApplicationService.increaseFollowCount(
//                        followEventDto.getFollowerId(),
//                        followEventDto.getFolloweeId()
//                );
//            } else if (followEventDto.getFollowType() == FollowType.UNFOLLOW) {
//                userApplicationService.decreaseFollowCount(
//                        followEventDto.getFollowerId(),
//                        followEventDto.getFolloweeId()
//                );
//            } else {
//                log.warn("[Kafka-Consumer] 알 수 없는 FollowType 수신: {}",
//                        followEventDto.getFollowType());
//                throw new RuntimeException("알 수 없는 FollowType: " + followEventDto.getFollowType());
//            }
//
//        } catch (Exception e) {
//            log.error("[Kafka-Consumer-User] 처리 중 예외 발생 - DLQ로 전송", e);
//            throw new RuntimeException(e);
//        }
//    }
    @KafkaListener(
            topics = "user.follow.count.changed",
            groupId = "user-service-group",
            containerFactory = "UserKafkaListenerContainerFactory"
    )
    public void consumeFollowEvent(String message) {
        log.info("[Kafka-Consumer-User] FollowEventDto 수신: {}", message);

        FollowEventDto followEventDto = deserializeMessage(message);

        try {
            log.info("[Kafka-Consumer-User] FOLLOW EVENT DTO: {}", followEventDto);
            if (followEventDto.getFollowType() == FollowType.FOLLOW) {
                followCountEventQueue.push(
                        new FollowCountDeltaDto(followEventDto.getFolloweeId(), TargetType.FOLLOWER,
                                +1));
                followCountEventQueue.push(new FollowCountDeltaDto(followEventDto.getFollowerId(),
                        TargetType.FOLLOWING, +1));
            } else if (followEventDto.getFollowType() == FollowType.UNFOLLOW) {
                followCountEventQueue.push(
                        new FollowCountDeltaDto(followEventDto.getFolloweeId(), TargetType.FOLLOWER,
                                -1));
                followCountEventQueue.push(new FollowCountDeltaDto(followEventDto.getFollowerId(),
                        TargetType.FOLLOWING, -1));
            } else {
                log.warn("[Kafka-Consumer] 알 수 없는 FollowType 수신: {}",
                        followEventDto.getFollowType());
                throw new RuntimeException("알 수 없는 FollowType: " + followEventDto.getFollowType());
            }

        } catch (Exception e) {
            log.error("[Kafka-Consumer-User] 처리 중 예외 발생 - DLQ로 전송", e);
            throw new RuntimeException(e);
        }
    }

    private FollowEventDto deserializeMessage(String message) {
        log.info("[Deserializer] 원본 메시지 수신: {}", message);
        try {
            // 이중 감싼 따옴표 제거 (앞뒤 따옴표가 있는 경우)
            if (message.startsWith("\"") && message.endsWith("\"")) {
                message = message.substring(1, message.length() - 1);
            }

            // 이스케이프 문자 제거
            message = message.replace("\\\"", "\"");

            log.info("[Deserializer] Unwrapped message: {}", message);

            return objectMapper.readValue(message, FollowEventDto.class);
        } catch (JsonProcessingException e) {
            log.error("Kafka 메시지 역직렬화 실패: {}", message, e);
            throw new CustomException(UserInfraExceptionCode.KAFKA_JSON_DESERIALIZATION_FAILED, e);
        }
    }
}
