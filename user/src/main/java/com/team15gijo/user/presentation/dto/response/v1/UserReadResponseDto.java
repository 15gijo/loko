package com.team15gijo.user.presentation.dto.response.v1;

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
                user.getUsername(),
                user.getNickname(),
                user.getEmail(),
                user.getProfile(),
                user.getRegion()
        );
    }
}
