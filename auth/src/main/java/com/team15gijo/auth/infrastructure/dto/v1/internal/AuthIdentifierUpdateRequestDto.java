package com.team15gijo.auth.infrastructure.dto.v1.internal;

public record AuthIdentifierUpdateRequestDto(
        Long userId,
        String password,
        String newIdentifier
) {

}
