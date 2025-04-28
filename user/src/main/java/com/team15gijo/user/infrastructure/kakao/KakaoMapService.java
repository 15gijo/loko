package com.team15gijo.user.infrastructure.kakao;

import com.team15gijo.common.exception.CustomException;
import com.team15gijo.user.application.dto.v2.KakaoMapRegionInfoCommand;
import com.team15gijo.user.infrastructure.dto.response.v1.KakaoMapAddressResponseDto;
import com.team15gijo.user.infrastructure.exception.UserInfraExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoMapService {

    private final KakaoMapClient kakaoMapClient;

    public String getAddress(String region) {
        KakaoMapAddressResponseDto kakaoMapAddressResponseDto = kakaoMapClient.searchAddress(
                region);

        log.info("KakaoMapAddressResponseDto={}", kakaoMapAddressResponseDto.getDocuments());

        return kakaoMapAddressResponseDto.getDocuments()
                .stream()
                .findFirst()
                .map(KakaoMapAddressResponseDto.Document::getAddressName)
                .orElseThrow(() -> new CustomException(UserInfraExceptionCode.ADDRESS_NOT_FOUND));
    }

    public KakaoMapRegionInfoCommand getRegionInfo(String region) {
        KakaoMapAddressResponseDto kakaoMapAddressResponseDto = kakaoMapClient.searchAddress(
                region);
        KakaoMapAddressResponseDto.Document document = kakaoMapAddressResponseDto.getDocuments()
                .get(0);

        log.info("KakaoMapAddressResponseDto={}", kakaoMapAddressResponseDto.getDocuments());

        return KakaoMapRegionInfoCommand.of(
                document.getAddress().getBCode(),
                document.getAddress().getRegion_1depth_name() + " " +
                        document.getAddress().getRegion_2depth_name() + " " +
                        document.getAddress().getRegion_3depth_name(),
                Double.valueOf(document.getX()),
                Double.valueOf(document.getY())
        );

    }
}
