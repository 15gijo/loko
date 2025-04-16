package com.team15gijo.user.presentation.dto.v1.request;

public record UserUpdateRequestDto(
        String username,
        String nickname,
        String profile,
        String region
) {

}
