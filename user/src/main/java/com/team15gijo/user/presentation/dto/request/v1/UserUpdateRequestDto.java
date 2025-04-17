package com.team15gijo.user.presentation.dto.request.v1;

public record UserUpdateRequestDto(
        String username,
        String nickname,
        String profile,
        String region
) {

}
