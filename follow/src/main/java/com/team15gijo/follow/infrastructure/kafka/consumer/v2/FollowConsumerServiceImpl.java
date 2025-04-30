package com.team15gijo.follow.infrastructure.kafka.consumer.v2;

import com.team15gijo.follow.infrastructure.kafka.dto.v2.FollowEventDto;
import com.team15gijo.follow.infrastructure.kafka.dto.v2.FollowEventDto.FollowType;
import com.team15gijo.follow.infrastructure.redis.repository.FollowRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FollowConsumerServiceImpl implements FollowConsumerService {

    private final FollowRedisRepository<String> followRedisRepository;

    @Override
    public void handleFollowCreated(FollowEventDto followEventDto) {
        if (followEventDto.followType() != FollowType.FOLLOW) {
            log.warn("[FollowConsumerService] 잘못된 FollowType (expected=FOLLOW) 수신: {}",
                    followEventDto);
            return;
        }
        log.info("[FollowConsumerService] 팔로우 카운트 증가 처리 시작: {}", followEventDto);
        // 팔로워 수 +1
        followRedisRepository.incrementFollowerCount(String.valueOf(followEventDto.followeeId()));
        // 팔로잉 수 +1
        followRedisRepository.incrementFollowingCount(String.valueOf(followEventDto.followerId()));
    }


    @Override
    public void handleFollowDeleted(FollowEventDto followEventDto) {
        if (followEventDto.followType() != FollowType.UNFOLLOW) {
            log.warn("[FollowConsumerService] 잘못된 FollowType (expected=UNFOLLOW) 수신: {}",
                    followEventDto);
            return;
        }

        log.info("[FollowConsumerService] 언팔로우 카운트 감소 처리 시작: {}", followEventDto);
        // 팔로워 수 -1
        followRedisRepository.decrementFollowerCount(
                String.valueOf(followEventDto.followeeId()));
        // 팔로잉 수 -1
        followRedisRepository.decrementFollowingCount(
                String.valueOf(followEventDto.followerId()));

    }
}
