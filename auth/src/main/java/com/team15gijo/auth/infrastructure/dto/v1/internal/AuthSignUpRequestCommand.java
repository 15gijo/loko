package com.team15gijo.auth.infrastructure.dto.v1.internal;

import com.team15gijo.auth.domain.model.LoginType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthSignUpRequestCommand {

    private final String nickname;
    private final String identifier;
    private final String password;
    private final LoginType loginType;

    public static AuthSignUpRequestCommand from(AuthSignUpRequestDto authSignUpRequestDto) {
        return new AuthSignUpRequestCommand(
                authSignUpRequestDto.getNickname(),
                authSignUpRequestDto.getIdentifier(),
                authSignUpRequestDto.getPassword(),
                //로그인 타입 이넘 검사 및 리턴
                LoginType.fromLoginTypeName(authSignUpRequestDto.getLoginTypeName())

        );
    }

}
