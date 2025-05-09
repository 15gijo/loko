package com.team15gijo.auth.infrastructure.security.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserProfile {

    private final String provider;
    private final String providerId;
    private final String email;
    private final String name;
    private final String nickname;
    private final String profileImageUrl;
}
