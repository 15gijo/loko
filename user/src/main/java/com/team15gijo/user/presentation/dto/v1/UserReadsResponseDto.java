package com.team15gijo.user.presentation.dto.v1;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserReadsResponseDto {

    private String nickname;
    private String username;
    private String profile;
    private String region;

    @QueryProjection
    public UserReadsResponseDto(String nickname, String username, String profile, String region) {
        this.nickname = nickname;
        this.username = username;
        this.profile = profile;
        this.region = region;
    }
}
