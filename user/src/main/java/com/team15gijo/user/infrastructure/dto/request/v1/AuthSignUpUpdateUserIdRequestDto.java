package com.team15gijo.user.infrastructure.dto.request.v1;

import java.util.UUID;

public record AuthSignUpUpdateUserIdRequestDto(
        UUID authId,
        Long userId
) {

}
