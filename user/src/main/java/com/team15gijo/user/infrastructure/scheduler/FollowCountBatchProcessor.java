package com.team15gijo.user.infrastructure.scheduler;

import com.team15gijo.user.domain.repository.UserRepository;
import com.team15gijo.user.infrastructure.scheduler.support.FollowCountDeltaDto;
import com.team15gijo.user.infrastructure.scheduler.support.FollowCountDeltaDto.TargetType;
import com.team15gijo.user.infrastructure.scheduler.support.FollowCountEventQueue;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class FollowCountBatchProcessor {

    private final FollowCountEventQueue followCountEventQueue;
    private final UserRepository userRepository;

    @Scheduled(fixedDelay = 10 * 1000) // 10초마다 실행
    @Transactional
    public void process() {
        log.info("[Batch-kafka] user DB followerCount/followingCount 업데이트 시작");
        List<FollowCountDeltaDto> events = followCountEventQueue.drainAll();

        if (events.isEmpty()) {
            return;
        }

        Map<Long, Long> followerMap = new HashMap<>();
        Map<Long, Long> followingMap = new HashMap<>();

        for (FollowCountDeltaDto event : events) {
            if (event.targetType() == TargetType.FOLLOWER) {
                followerMap.merge(event.userId(), event.delta(), Long::sum);
            } else {
                followingMap.merge(event.userId(), event.delta(), Long::sum);
            }
        }

        if (!followerMap.isEmpty()) {
            log.info("[Batch 업데이트] followerMap = {}", followerMap);
            userRepository.bulkUpdateFollowerCounts(followerMap);
        }

        if (!followingMap.isEmpty()) {
            log.info("[Batch 업데이트] followerMap = {}", followingMap);
            userRepository.bulkUpdateFollowingCounts(followingMap);
        }
    }
}
