package com.team15gijo.post.domain.repository.v2.dto;

import java.util.UUID;

public record PostSummaryDto(
        UUID postId,
        String username,
        String region,
        int views,
        int commentCount,
        int likeCount
) {}