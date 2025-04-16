package com.team15gijo.user.presentation.dto.v1.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record AdminUserStatusUpdateRequestDto(
        @NotNull(message = "유저 아이디는 필수 입력값 입니다.")
        Long userId,

        @NotBlank(message = "유저 상태값 이름은 필수 입력값 입니다.")
        @Pattern(regexp = "^(활성화 유저|차단 유저)$", message = "유효한 유저 상태만 입력 가능합니다.")
        String targetUserStatusName
) {

}
