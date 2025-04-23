package com.team15gijo.follow.presentation.dto.response.v2;

import com.team15gijo.follow.domain.model.FollowEntity;
import com.team15gijo.follow.domain.model.FollowStatus;
import java.util.UUID;

public record UnfollowResponseDto(
        UUID followId,
        Long followerId,
        Long followeeId,
        FollowStatus followStatus
) {

    public static UnfollowResponseDto from(FollowEntity followEntity) {
        return new UnfollowResponseDto(
                followEntity.getId(),
                followEntity.getFollowerId(),
                followEntity.getFolloweeId(),
                followEntity.getFollowStatus()
        );
    }
}
