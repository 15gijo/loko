package com.team15gijo.user.presentation.dto.internal.request.v1;

import java.util.List;

public record UserAndRegionInfoRequestDto(
        Long myUserId,
        List<Long> candidateUserIds
) {

}

