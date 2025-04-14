package com.team15gijo.user.presentation.dto.v1;

import com.team15gijo.user.domain.model.UserEntity;

public record AdminUserReadResponseDto(

        Long userId,
        String username,
        String nickname,
        String email,
        String profile,
        String userStatusName,
        String region

) {

    public static AdminUserReadResponseDto from(UserEntity user) {
        return new AdminUserReadResponseDto(
                user.getId(),
                user.getUserName(),
                user.getNickName(),
                user.getEmail(),
                user.getProfile(),
                user.getStatus().getUserStatusName(),
                user.getRegion()
        );
    }
}
