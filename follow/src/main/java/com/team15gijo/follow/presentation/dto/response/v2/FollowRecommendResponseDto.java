package com.team15gijo.follow.presentation.dto.response.v2;

public record FollowRecommendResponseDto(
        Long userId,
        String nickname,
        String username,
        String profile,
        String region,
        int mutualFriends
) {

}
