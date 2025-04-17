package com.team15gijo.auth.presentation.dto.request.v1;

import jakarta.validation.constraints.NotBlank;

public record AuthLoginRequestDto(

        @NotBlank(message = "아이디(이메일)은 필수 입니다.")
        String identifier,

        @NotBlank(message = "비밀번호는 필수 입니다.")
        String password
) {

}
