package com.team15gijo.user.infrastructure.dto.request.v1;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AuthSignUpRequestDto {

    String nickname;
    String identifier;
    String password;
    String loginTypeName;

    @Builder
    public AuthSignUpRequestDto(
            String nickname,
            String identifier,
            String password,
            String loginTypeName) {
        this.nickname = nickname;
        this.identifier = identifier;
        this.password = password;
        this.loginTypeName = loginTypeName;
    }
}

