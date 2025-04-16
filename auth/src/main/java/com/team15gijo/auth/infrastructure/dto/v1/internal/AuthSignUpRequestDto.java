package com.team15gijo.auth.infrastructure.dto.v1.internal;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AuthSignUpRequestDto {

    //    @NotBlank(message = "닉네임은 필수입니다.")
    private String nickname;

    //    @NotBlank(message = "identifier는 필수입니다.")
    private String identifier;

    //    @NotBlank(message = "password는 필수입니다.")
    private String password;

    //    @NotBlank(message = "loginTypeName은 필수입니다.")
    private String loginTypeName;

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
