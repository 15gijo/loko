package com.team15gijo.follow.application.dto.v2;

import com.team15gijo.follow.domain.model.FollowStatus;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminFollowSearchCommand {

    private final UUID followId;
    private final Long followerId;
    private final Long followeeId;
    private final FollowStatus followStatus;


}
