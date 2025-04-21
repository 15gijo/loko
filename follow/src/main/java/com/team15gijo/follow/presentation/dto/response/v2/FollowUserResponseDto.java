package com.team15gijo.follow.presentation.dto.response.v2;

public record FollowUserResponseDto(
        Long userId,
        String username,
        String nickname,
        String profile,
        Boolean isMutual
) {

    public static FollowUserResponseDto of(
            Long userId,
            String username,
            String nickname,
            String profile,
            boolean isMutual) {
        return new FollowUserResponseDto(userId, username, nickname, profile, isMutual);
    }
}
