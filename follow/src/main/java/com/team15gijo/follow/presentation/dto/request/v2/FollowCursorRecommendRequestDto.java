package com.team15gijo.follow.presentation.dto.request.v2;

import com.team15gijo.follow.domain.model.RecommendPriority;

public record FollowCursorRecommendRequestDto(
        Long lastUserId,
        Integer size,
        RecommendPriority recommendPriority
) {

}
