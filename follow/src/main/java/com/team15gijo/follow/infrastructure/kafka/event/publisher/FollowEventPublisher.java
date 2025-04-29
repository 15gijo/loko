package com.team15gijo.follow.infrastructure.kafka.event.publisher;

import com.team15gijo.follow.infrastructure.kafka.event.model.FollowCreatedEvent;
import com.team15gijo.follow.infrastructure.kafka.event.model.FollowDeletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FollowEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void publishFollowCreated(Long followerId, Long followeeId) {
        applicationEventPublisher.publishEvent(new FollowCreatedEvent(followerId, followeeId));
    }

    public void publishFollowDeleted(Long followerId, Long followeeId) {
        applicationEventPublisher.publishEvent(new FollowDeletedEvent(followerId, followeeId));
    }

}
