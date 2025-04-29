package com.team15gijo.follow.infrastructure.persistence;

import com.team15gijo.follow.application.dto.v2.AdminFollowSearchCommand;
import com.team15gijo.follow.application.dto.v2.FollowCursorCandidateResult;
import com.team15gijo.follow.domain.model.FollowEntity;
import com.team15gijo.follow.domain.model.FollowStatus;
import com.team15gijo.follow.domain.repository.FollowRepository;
import com.team15gijo.follow.infrastructure.persistence.jpa.FollowJpaRepository;
import com.team15gijo.follow.infrastructure.persistence.querydsl.FollowQueryDslRepository;
import com.team15gijo.follow.presentation.dto.response.v2.AdminFollowSearchResponseDto;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FollowRepositoryImpl implements FollowRepository {

    private final FollowJpaRepository followJpaRepository;
    private final FollowQueryDslRepository followQueryDslRepository;

    @Override
    public boolean existsByFollowerIdAndFolloweeId(Long followerId, Long followeeId) {
        return followJpaRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
    }

    @Override
    public FollowEntity save(FollowEntity follow) {
        return followJpaRepository.save(follow);
    }

    @Override
    public Optional<FollowEntity> findByFollowerIdAndFolloweeId(Long followerId, Long followeeId) {
        return followJpaRepository.findByFollowerIdAndFolloweeId(followerId, followeeId);
    }

    @Override
    public void delete(FollowEntity follow) {
        followJpaRepository.delete(follow);
    }

    @Override
    public Optional<FollowEntity> findDeletedByFollowerIdAndFolloweeId(Long followerId,
            Long followeeId) {
        return followJpaRepository.findDeletedByFollowerIdAndFolloweeId(followerId, followeeId);
    }

    @Override
    public void restoreFollow(UUID id) {
        followJpaRepository.restoreFollow(id, FollowStatus.FOLLOW);
    }

    @Override
    public Page<FollowEntity> findAllByFollowerIdAndFollowStatus(Long followerId,
            FollowStatus followStatus, Pageable validatePageable) {
        return followJpaRepository.findAllByFollowerIdAndFollowStatus(followerId, followStatus,
                validatePageable);
    }

    @Override
    public Page<FollowEntity> findAllByFolloweeIdAndFollowStatus(Long followeeId,
            FollowStatus followStatus, Pageable validatePageable) {
        return followJpaRepository.findAllByFolloweeIdAndFollowStatus(followeeId, followStatus,
                validatePageable);
    }

    @Override
    public long countByFolloweeIdAndFollowStatus(Long followeeId, FollowStatus followStatus) {
        return followJpaRepository.countByFolloweeIdAndFollowStatus(followeeId, followStatus);
    }

    @Override
    public long countByFollowerIdAndFollowStatus(Long followerId, FollowStatus followStatus) {
        return followJpaRepository.countByFollowerIdAndFollowStatus(followerId, followStatus);
    }

    @Override
    public Page<AdminFollowSearchResponseDto> searchAllFollowsForAdmin(
            AdminFollowSearchCommand adminFollowSearchCommand, Pageable validatePageable) {
        return followQueryDslRepository.searchAllFollowsForAdmin(adminFollowSearchCommand,
                validatePageable);
    }

    @Override
    public FollowCursorCandidateResult find2HopCandidateUserIds(Long userId, Long lastUserId,
            Pageable validatePageable) {
        return followQueryDslRepository.find2HopCandidateUserIds(userId, lastUserId,
                validatePageable);
    }

    @Override
    public void saveAndFlush(FollowEntity follow) {
        followJpaRepository.saveAndFlush(follow);
    }

    @Override
    public boolean existsByFollowerIdAndFolloweeIdAndFollowStatus(Long followerId, Long followeeId,
            FollowStatus followStatus) {
        return followJpaRepository.existsByFollowerIdAndFolloweeIdAndFollowStatus(followerId,
                followeeId, followStatus);
    }
}
