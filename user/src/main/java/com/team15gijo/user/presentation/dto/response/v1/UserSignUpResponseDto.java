package com.team15gijo.user.presentation.dto.response.v1;

import java.util.UUID;

public record UserSignUpResponseDto(
        String email,
        String nickname,
        String username,
        String region,
        String profile,
        UUID regionId
) {

}
