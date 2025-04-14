package com.team15gijo.user.presentation.dto.v1;

import com.team15gijo.user.domain.model.UserEntity;

public record UserReadResponseDto(
        String username,
        String nickname,
        String email,
        String profile,
        String region
) {

    public static UserReadResponseDto from(UserEntity user) {
        return new UserReadResponseDto(
                user.getUserName(),
                user.getNickName(),
                user.getEmail(),
                user.getProfile(),
                user.getRegion()
        );
    }
}
