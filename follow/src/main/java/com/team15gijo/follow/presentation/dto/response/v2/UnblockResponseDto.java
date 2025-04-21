package com.team15gijo.follow.presentation.dto.response.v2;

import com.team15gijo.follow.domain.model.FollowEntity;
import com.team15gijo.follow.domain.model.FollowStatus;
import java.util.UUID;

public record UnblockResponseDto(
        UUID followId,
        Long unblockerId,
        Long unblockedId,
        FollowStatus followStatus
) {

    public static UnblockResponseDto from(FollowEntity savedFollow) {
        return new UnblockResponseDto(
                savedFollow.getId(),
                savedFollow.getFollowerId(),
                savedFollow.getFolloweeId(),
                savedFollow.getFollowStatus()
        );
    }
}
