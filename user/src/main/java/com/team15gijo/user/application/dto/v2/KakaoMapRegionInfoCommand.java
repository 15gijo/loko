package com.team15gijo.user.application.dto.v2;

public record KakaoMapRegionInfoCommand(
        String regionCode,
        String regionName,
        Double longitude,
        Double latitude
) {

    public static KakaoMapRegionInfoCommand of(
            String regionCode,
            String regionName,
            Double longitude,
            Double latitude) {
        return new KakaoMapRegionInfoCommand(regionCode, regionName, longitude, latitude);
    }
}
