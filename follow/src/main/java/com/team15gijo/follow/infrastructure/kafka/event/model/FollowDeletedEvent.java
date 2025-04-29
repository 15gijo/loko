package com.team15gijo.follow.infrastructure.kafka.event.model;

public record FollowDeletedEvent(
        Long followerId,
        Long followeeId
) {

}
