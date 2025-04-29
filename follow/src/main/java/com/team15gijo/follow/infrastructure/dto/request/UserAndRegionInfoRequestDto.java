package com.team15gijo.follow.infrastructure.dto.request;

import java.util.List;

public record UserAndRegionInfoRequestDto(
        Long myUserId,
        List<Long> candidateUserIds
) {

}
