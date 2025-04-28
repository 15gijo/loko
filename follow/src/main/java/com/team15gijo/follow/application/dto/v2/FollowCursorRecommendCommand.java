package com.team15gijo.follow.application.dto.v2;

import com.team15gijo.follow.domain.model.RecommendPriority;
import com.team15gijo.follow.infrastructure.dto.response.v2.UserAndRegionInfoFollowResponseDto;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FollowCursorRecommendCommand {

    private final Long userId;
    private final List<Long> candidateUserIds;
    private final List<UserAndRegionInfoFollowResponseDto> userAndRegionInfos;
    private final Map<Long, Integer> candidateCount;
    private final RecommendPriority recommendPriority;

    public static FollowCursorRecommendCommand of(
            Long userId,
            List<Long> candidateUserIds,
            List<UserAndRegionInfoFollowResponseDto> userAndRegionInfos,
            Map<Long, Integer> candidateCount,
            RecommendPriority recommendPriority) {
        return new FollowCursorRecommendCommand(
                userId,
                candidateUserIds,
                userAndRegionInfos,
                candidateCount,
                recommendPriority
        );
    }

}
