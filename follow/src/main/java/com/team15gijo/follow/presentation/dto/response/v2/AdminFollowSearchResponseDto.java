package com.team15gijo.follow.presentation.dto.response.v2;

import com.querydsl.core.annotations.QueryProjection;
import com.team15gijo.follow.domain.model.FollowStatus;
import java.util.UUID;

public record AdminFollowSearchResponseDto(
        UUID followId,
        Long followerId,
        Long followeeId,
        FollowStatus followStatus
) {

    @QueryProjection
    public AdminFollowSearchResponseDto {
    }
}
