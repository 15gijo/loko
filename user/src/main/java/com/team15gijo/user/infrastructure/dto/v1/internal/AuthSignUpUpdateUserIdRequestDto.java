package com.team15gijo.user.infrastructure.dto.v1.internal;

import java.util.UUID;

public record AuthSignUpUpdateUserIdRequestDto(
        UUID authId,
        Long userId
) {

}
