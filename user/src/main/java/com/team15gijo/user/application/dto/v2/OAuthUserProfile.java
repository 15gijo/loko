package com.team15gijo.user.application.dto.v2;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OAuthUserProfile {

    private final String provider;
    private final String providerId;
    private final String email;
    private final String name;
    private final String nickname;
    private final String profileImageUrl;
}
