package com.team15gijo.follow.presentation.dto.request.v2;

import jakarta.validation.constraints.NotNull;

public record BlockRequestDto(
        @NotNull(message = "차단할 유저 ID는 필수 입니다.")
        Long blockUserID
) {

}
