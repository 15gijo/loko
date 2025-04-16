package com.team15gijo.user.presentation.dto.v1.response;

public record UserSignUpResponseDto(
        String email,
        String nickname,
        String username,
        String region,
        String profile
) {

}
