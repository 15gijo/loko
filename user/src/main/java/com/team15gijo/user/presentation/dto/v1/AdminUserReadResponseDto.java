package com.team15gijo.user.presentation.dto.v1;

import com.querydsl.core.annotations.QueryProjection;
import com.team15gijo.user.domain.model.UserEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminUserReadResponseDto {

    private Long userId;
    private String username;
    private String nickname;
    private String email;
    private String profile;
    private String userStatusName;
    private String region;

    @QueryProjection
    public AdminUserReadResponseDto(Long userId, String username, String nickname, String email,
            String profile, String userStatusName, String region) {
        this.userId = userId;
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.profile = profile;
        this.userStatusName = userStatusName;
        this.region = region;

    }

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
