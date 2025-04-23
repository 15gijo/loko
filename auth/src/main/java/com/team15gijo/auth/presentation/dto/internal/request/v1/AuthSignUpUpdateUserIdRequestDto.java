package com.team15gijo.auth.presentation.dto.internal.request.v1;

import java.util.UUID;

public record AuthSignUpUpdateUserIdRequestDto(
        UUID authId,
        Long userId
) {

}
