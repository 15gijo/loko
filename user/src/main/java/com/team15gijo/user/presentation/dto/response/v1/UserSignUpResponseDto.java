package com.team15gijo.user.presentation.dto.response.v1;

public record UserSignUpResponseDto(
        String email,
        String nickname,
        String username,
        String region,
        String profile
) {

}
