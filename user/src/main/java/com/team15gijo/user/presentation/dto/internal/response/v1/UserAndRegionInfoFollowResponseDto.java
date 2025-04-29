package com.team15gijo.user.presentation.dto.internal.response.v1;

import com.querydsl.core.annotations.QueryProjection;

public record UserAndRegionInfoFollowResponseDto(
        Long userId,
        String nickname,
        String username,
        String profile,
        String regionCode,
        String regionName,
        Double distanceKm
) {

    @QueryProjection
    public UserAndRegionInfoFollowResponseDto(
            Long userId,
            String nickname,
            String username,
            String profile,
            String regionCode,
            String regionName,
            Double distanceKm
    ) {
        this.userId = userId;
        this.nickname = nickname;
        this.username = username;
        this.profile = profile;
        this.regionCode = regionCode;
        this.regionName = regionName;
        this.distanceKm = distanceKm;
    }
}

