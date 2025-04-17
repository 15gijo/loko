package com.team15gijo.user.presentation.dto.request.v1;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserEmailUpdateRequestDto(

        @Email(message = "유효한 이메일 주소를 입력해주세요.")
        @NotBlank(message = "이메일은 필수입니다.")
        String currentEmail,

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 8, max = 15, message = "비밀번호는 8자 이상 15자 이하로 입력해주세요.")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}|\\[\\]:\";'<>?,./]).{8,15}$",
                message = "비밀번호는 대소문자, 숫자, 특수문자를 모두 포함해야 합니다."
        )
        String password,

        @Email(message = "유효한 이메일 주소를 입력해주세요.")
        @NotBlank(message = "새로운 이메일은 필수입니다.")
        String newEmail
) {

}
