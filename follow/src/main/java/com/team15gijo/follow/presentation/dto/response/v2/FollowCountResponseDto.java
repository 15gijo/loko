package com.team15gijo.follow.presentation.dto.response.v2;

public record FollowCountResponseDto(
        long followerCount,
        long followingCount
) {

    public static FollowCountResponseDto of(long followerCount, long followingCount) {
        return new FollowCountResponseDto(followerCount, followingCount);
    }
}
