package com.team15gijo.user.presentation.dto.response.v1;

import com.team15gijo.user.domain.model.UserEntity;

public record UserUpdateResponseDto(
        String username,
        String nickname,
        String email,
        String profile,
        String region

) {

    public static UserUpdateResponseDto from(UserEntity user) {
        return new UserUpdateResponseDto(
                user.getUsername(),
                user.getNickname(),
                user.getEmail(),
                user.getProfile(),
                user.getRegion()
        );
    }
}
