package com.team15gijo.user.presentation.dto.v1.internal.request;

public record AuthIdentifierUpdateRequestDto(
        Long userId,
        String password,
        String newIdentifier
) {

}
