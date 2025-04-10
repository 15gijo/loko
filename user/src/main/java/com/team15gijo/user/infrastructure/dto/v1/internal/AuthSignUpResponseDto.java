package com.team15gijo.user.infrastructure.dto.v1.internal;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AuthSignUpResponseDto {

    String message;
    String code;

    @Builder
    public AuthSignUpResponseDto(
            String message,
            String code) {
        this.message = message;
        this.code = code;
    }
}
