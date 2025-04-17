package com.team15gijo.auth.presentation.dto.internal.request.v1;

public record AuthPasswordUpdateRequestDto(
        Long userId,
        String currentPassword,
        String newPassword
) {

}

