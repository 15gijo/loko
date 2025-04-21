package com.team15gijo.follow.domain.service;

import com.team15gijo.follow.domain.model.FollowEntity;
import com.team15gijo.follow.presentation.dto.request.v2.FollowRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FollowDomainService {

    FollowEntity createOrRestoreFollow(Long followerId, FollowRequestDto followRequestDto);

    FollowEntity deleteFollow(Long followerId, Long followeeId);

    FollowEntity blockFollow(Long followerId, Long followeeId);

    FollowEntity unblockFollow(Long followerId, Long followeeId);

    Page<FollowEntity> getMyFollowings(Long followerId, Pageable validatePageable);

    Page<FollowEntity> getMyFollowers(Long followeeId, Pageable validatePageable);

    long countFollowers(Long userId);

    long countFollowings(Long userId);
}
