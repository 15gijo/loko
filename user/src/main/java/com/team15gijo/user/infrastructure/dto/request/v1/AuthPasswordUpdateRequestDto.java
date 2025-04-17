package com.team15gijo.user.infrastructure.dto.request.v1;

public record AuthPasswordUpdateRequestDto(
        Long userId,
        String currentPassword,
        String newPassword
) {

}
