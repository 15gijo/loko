package com.team15gijo.follow.application.dto.v2;

import java.util.List;

public record FollowCursorCandidateResult(
        List<Long> userIds,
        boolean hasNext
) {

}
