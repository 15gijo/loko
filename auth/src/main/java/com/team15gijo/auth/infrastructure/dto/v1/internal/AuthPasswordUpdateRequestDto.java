package com.team15gijo.auth.infrastructure.dto.v1.internal;

public record AuthPasswordUpdateRequestDto(
        Long userId,
        String currentPassword,
        String newPassword
) {

}

