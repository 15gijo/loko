package com.team15gijo.user.infrastructure.dto.request.v1;

public record AuthIdentifierUpdateRequestDto(
        Long userId,
        String password,
        String newIdentifier
) {

}
