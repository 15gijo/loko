package com.team15gijo.follow.presentation.dto.response.v2;

import java.util.List;

public record FollowCursorRecommendResponseDto<T>(
        List<T> content,
        Long nextCursor,
        boolean hasNext
) {

}
