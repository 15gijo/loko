package com.team15gijo.follow.application.service;

import com.team15gijo.follow.domain.model.FollowStatus;
import com.team15gijo.follow.domain.model.RecommendPriority;
import com.team15gijo.follow.presentation.dto.request.v2.BlockRequestDto;
import com.team15gijo.follow.presentation.dto.request.v2.FollowRequestDto;
import com.team15gijo.follow.presentation.dto.response.v2.AdminFollowSearchResponseDto;
import com.team15gijo.follow.presentation.dto.response.v2.BlockResponseDto;
import com.team15gijo.follow.presentation.dto.response.v2.FollowCountResponseDto;
import com.team15gijo.follow.presentation.dto.response.v2.FollowRecommendResponseDto;
import com.team15gijo.follow.presentation.dto.response.v2.FollowResponseDto;
import com.team15gijo.follow.presentation.dto.response.v2.FollowUserResponseDto;
import com.team15gijo.follow.presentation.dto.response.v2.UnblockResponseDto;
import com.team15gijo.follow.presentation.dto.response.v2.UnfollowResponseDto;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface FollowApplicationService {

    FollowResponseDto createFollow(Long followerId, FollowRequestDto followRequestDto);

    UnfollowResponseDto deleteFollow(Long followerId, Long followeeId);

    BlockResponseDto blockFollow(Long followerId, BlockRequestDto blockRequestDto);

    UnblockResponseDto unblockFollow(Long followerId, Long followeeId);

    Page<FollowUserResponseDto> getMyFollowings(Long followerId, Pageable validatePageable);

    Page<FollowUserResponseDto> getMyFollowers(Long followeeId, Pageable validatePageable);

    FollowCountResponseDto getCountMyFollows(Long userId);

    Page<FollowUserResponseDto> getFollowings(Long targetUserId, Pageable validatePageable);

    Page<FollowUserResponseDto> getFollowers(Long targetUserId, Pageable validatePageable);

    FollowCountResponseDto getCountFollowers(Long targetUserId);

    Page<AdminFollowSearchResponseDto> searchAllFollows(UUID followId, Long followerId,
            Long followeeId, FollowStatus followStatus, Pageable validatePageable);

    Slice<FollowRecommendResponseDto> recommend(Long userId, Long lastUserId,
            RecommendPriority priority, Pageable validatePageable);
}
