package com.team15gijo.user.infrastructure.kakao;

import com.team15gijo.user.infrastructure.dto.response.v1.KakaoMapAddressResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoMapClient {

    private final WebClient webClient;

    @Value("${kakao.api.key}")
    private String kakaoKey;

    public KakaoMapAddressResponseDto searchAddress(String query) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("dapi.kakao.com")
                        .path("/v2/local/search/address.json")
                        .queryParam("query", query)
                        .build())
                .header("Authorization", "KakaoAK " + kakaoKey)
                .retrieve()
                .bodyToMono(KakaoMapAddressResponseDto.class)
                .block();
    }
}
