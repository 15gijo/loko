package com.team15gijo.user.presentation.dto.internal.response.v1;

import com.querydsl.core.annotations.QueryProjection;

public record UserAndRegionInfoFollowResponseDto(
        Long userId,
        String nickname,
        String username,
        String profile,
        String regionCode,
        String regionName
) {

    @QueryProjection
    public UserAndRegionInfoFollowResponseDto(
            Long userId,
            String nickname,
            String username,
            String profile,
            String regionCode,
            String regionName
    ) {
        this.userId = userId;
        this.nickname = nickname;
        this.username = username;
        this.profile = profile;
        this.regionCode = regionCode;
        this.regionName = regionName;
    }
}

