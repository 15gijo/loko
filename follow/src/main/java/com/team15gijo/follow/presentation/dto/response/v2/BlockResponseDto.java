package com.team15gijo.follow.presentation.dto.response.v2;

import com.team15gijo.follow.domain.model.FollowEntity;
import com.team15gijo.follow.domain.model.FollowStatus;
import java.util.UUID;

public record BlockResponseDto(
        UUID followId,
        Long blockerId,
        Long blockedId,
        FollowStatus followStatus
) {

    public static BlockResponseDto from(FollowEntity savedFollow) {
        return new BlockResponseDto(
                savedFollow.getId(),
                savedFollow.getFollowerId(),
                savedFollow.getFolloweeId(),
                savedFollow.getFollowStatus()
        );
    }
}
