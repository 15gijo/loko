package com.team15gijo.follow.infrastructure.kafka.event.model;

public record FollowCreatedEvent(
        Long followerId,
        Long followeeId
) {

}
