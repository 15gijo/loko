package com.team15gijo.follow.infrastructure.dto.response.v2;

public record UserInfoFollowerResponseDto(
        Long userId,
        String username,
        String nickname,
        String profile
) {

}
