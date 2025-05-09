package com.team15gijo.user.infrastructure.dto.request.v1;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OAuthAuthSignUpRequestDto {
    String nickname;
    String identifier;
    String password;
    String loginTypeName;
    String oauthId;

    @Builder
    public OAuthAuthSignUpRequestDto(
            String nickname,
            String identifier,
            String password,
            String loginTypeName,
            String oauthId) {
        this.nickname = nickname;
        this.identifier = identifier;
        this.password = password;
        this.loginTypeName = loginTypeName;
        this.oauthId = oauthId;
    }
}
