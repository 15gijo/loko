package com.team15gijo.follow.infrastructure.kafka.event.listener;

import com.team15gijo.follow.infrastructure.kafka.producer.v2.FollowProducerService;
import com.team15gijo.follow.infrastructure.kafka.event.model.FollowCreatedEvent;
import com.team15gijo.follow.infrastructure.kafka.event.model.FollowDeletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service
@RequiredArgsConstructor
public class FollowEventListener {

    private final FollowProducerService followProducerService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleFollowCreated(FollowCreatedEvent event) {
        log.info("[팔로우 이벤트 리스너] 팔로우 이벤트 리스너 실행: followerId={}, followeeId={}", event.followerId(),
                event.followeeId());
        followProducerService.sendFollowCreated(event.followerId(), event.followeeId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleFollowDeleted(FollowDeletedEvent event) {
        log.info("[팔로우 이벤트 리스너] 언팔로우 이벤트 리스너 실행: followerId={}, followeeId={}", event.followerId(),
                event.followeeId());
        followProducerService.sendFollowDeleted(event.followerId(), event.followeeId());
    }

}
