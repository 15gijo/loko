package com.team15gijo.user.presentation.dto.v1.internal.request;

public record AuthPasswordUpdateRequestDto(
        Long userId,
        String currentPassword,
        String newPassword
) {

}
