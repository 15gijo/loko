package com.team15gijo.auth.presentation.dto.internal.request.v1;

public record AuthIdentifierUpdateRequestDto(
        Long userId,
        String password,
        String newIdentifier
) {

}
