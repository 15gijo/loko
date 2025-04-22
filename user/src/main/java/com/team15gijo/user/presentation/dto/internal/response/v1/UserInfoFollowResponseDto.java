package com.team15gijo.user.presentation.dto.internal.response.v1;

public record UserInfoFollowResponseDto(
        Long userId,
        String username,
        String nickname,
        String profile
) {

}
