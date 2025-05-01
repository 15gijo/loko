package com.team15gijo.user.infrastructure.scheduler.support;

public record FollowCountDeltaDto(
        Long userId,
        TargetType targetType,
        long delta
) {
    public enum TargetType {
        FOLLOWER, FOLLOWING
    }
}
