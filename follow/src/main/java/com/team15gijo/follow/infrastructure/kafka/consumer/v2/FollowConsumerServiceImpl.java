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
        log.info("팔로우 생성 처리: {}", followEventDto);
        if (followEventDto.followType() == FollowType.FOLLOW) {
            // 팔로워 수 +1
            followRedisRepository.incrementFollowerCount(String.valueOf(followEventDto.followeeId()));
            // 팔로잉 수 +1
            followRedisRepository.incrementFollowingCount(String.valueOf(followEventDto.followerId()));
        }
    }

    @Override
    public void handleFollowDeleted(FollowEventDto followEventDto) {
        log.info("언팔로우 생성 처리: {}", followEventDto);
        if (followEventDto.followType() == FollowType.UNFOLLOW) {
            // 팔로워 수 -1
            followRedisRepository.decrementFollowerCount(String.valueOf(followEventDto.followeeId()));
            // 팔로잉 수 -1
            followRedisRepository.decrementFollowingCount(String.valueOf(followEventDto.followerId()));
        }
    }
}
