package com.team15gijo.user.presentation.dto.request.v1;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserOAuthSignUpRequestDto(

        @NotBlank(message = "회원가입 토큰값은 필수입니다.")
        String signUpToken,

        @NotBlank(message = "이름은 필수입니다.")
        String username,

        @NotBlank(message = "지역은 필수입니다.")
        @Pattern(
                regexp = ".*(구|군)$",
                message = "주소는 반드시 ~구나 ~군으로 끝나야 합니다."
        )
        String region

) {

}
