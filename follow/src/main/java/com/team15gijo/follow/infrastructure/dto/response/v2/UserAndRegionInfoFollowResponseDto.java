package com.team15gijo.follow.infrastructure.dto.response.v2;

public record UserAndRegionInfoFollowResponseDto(
        Long userId,
        String nickname,
        String username,
        String profile,
        String regionCode,
        String regionName,
        Double distanceKm
) {

}
