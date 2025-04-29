package com.team15gijo.follow.infrastructure.kafka.dto.v2;

import java.time.LocalDateTime;

public record FollowEventDto(
        Long followerId,
        Long followeeId,
        FollowType followType,
        LocalDateTime timeStamp
) {

    public static FollowEventDto of(
            Long followerId,
            Long followeeId,
            FollowType followType,
            LocalDateTime timeStamp) {
        return new FollowEventDto(followerId, followeeId, followType, timeStamp);
    }

    public enum FollowType {
        FOLLOW,
        UNFOLLOW
    }
}
