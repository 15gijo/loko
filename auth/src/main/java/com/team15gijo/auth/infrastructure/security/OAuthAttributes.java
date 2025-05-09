package com.team15gijo.auth.infrastructure.security;

import com.team15gijo.auth.infrastructure.exception.AuthInfraExceptionCode;
import com.team15gijo.auth.infrastructure.security.dto.UserProfile;
import com.team15gijo.common.exception.CustomException;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum OAuthAttributes {

    GOOGLE((Map<String, Object> attributes) -> {
        return UserProfile.builder()
                .provider("google")
                .providerId((String) attributes.get("sub"))
                .email((String) attributes.get("email"))
                .name((String) attributes.get("name"))
                .profileImageUrl((String) attributes.get("picture"))
                .build();

    }),

    NAVER((Map<String, Object> attributes) -> {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return UserProfile.builder()
                .provider("naver")
                .providerId((String) response.get("id"))
                .email((String) response.get("email"))
                .name((String) response.get("name"))
                .nickname((String) response.get("nickname"))
                .profileImageUrl((String) response.get("profile_image"))
                .build();
    }),

    KAKAO((Map<String, Object> attributes) -> {
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) account.get("profile");
        return UserProfile.builder()
                .provider("kakao")
                .providerId(attributes.get("id").toString())
                .email((String) account.get("email"))
                .nickname((String) profile.get("nickname"))
                .profileImageUrl((String) profile.get("profile_image_url"))
                .build();
    });

    private final Function<Map<String, Object>, UserProfile> extractor;

    public static UserProfile extract(String registrationId, Map<String, Object> attributes) {
        return Arrays.stream(values())
                .filter(attr -> attr.name().equalsIgnoreCase(registrationId))
                .findFirst()
                .orElseThrow(() -> new CustomException(
                        AuthInfraExceptionCode.UNSUPPORTED_OAUTH2_PROVIDER))
                .extractor.apply(attributes);
    }
}
