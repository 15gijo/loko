package com.team15gijo.auth.presentation.dto.request.v1;

import jakarta.validation.constraints.NotNull;

public record AdminAssignManagerRequestDto(

        @NotNull(message = "권한을 부여할 유저 ID는 필수입니다.")
        Long targetUserId
) {

}
