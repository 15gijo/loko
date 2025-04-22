package com.team15gijo.follow.presentation.dto.request.v2;

import jakarta.validation.constraints.NotNull;

public record FollowRequestDto(
        @NotNull(message = "팔로우할 유저 ID는 필수 입니다.")
        Long followeeId
) {

}
