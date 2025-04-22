package com.team15gijo.follow.domain.repository;

import com.team15gijo.follow.application.dto.v2.AdminFollowSearchCommand;
import com.team15gijo.follow.domain.model.FollowEntity;
import com.team15gijo.follow.domain.model.FollowStatus;
import com.team15gijo.follow.presentation.dto.response.v2.AdminFollowSearchResponseDto;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FollowRepository {

    boolean existsByFollowerIdAndFolloweeId(Long followerId, Long followeeId);

    FollowEntity save(FollowEntity follow);

    Optional<FollowEntity> findByFollowerIdAndFolloweeId(Long followerId, Long followeeId);

    void delete(FollowEntity follow);

    Optional<FollowEntity> findDeletedByFollowerIdAndFolloweeId(Long followerId, Long followeeId);

    void restoreFollow(UUID id);

    Page<FollowEntity> findAllByFollowerIdAndFollowStatus(Long followerId, FollowStatus followStatus, Pageable validatePageable);

    Page<FollowEntity> findAllByFolloweeIdAndFollowStatus(Long followeeId, FollowStatus followStatus, Pageable validatePageable);

    long countByFolloweeIdAndFollowStatus(Long followeeId, FollowStatus followStatus);

    long countByFollowerIdAndFollowStatus(Long followerId, FollowStatus followStatus);

    Page<AdminFollowSearchResponseDto> searchAllFollowsForAdmin(AdminFollowSearchCommand adminFollowSearchCommand, Pageable validatePageable);
}
