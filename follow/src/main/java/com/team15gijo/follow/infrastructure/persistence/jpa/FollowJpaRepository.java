package com.team15gijo.follow.infrastructure.persistence.jpa;

import com.team15gijo.follow.domain.model.FollowEntity;
import com.team15gijo.follow.domain.model.FollowStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FollowJpaRepository extends JpaRepository<FollowEntity, UUID> {

    boolean existsByFollowerIdAndFolloweeId(Long followerId, Long followeeId);

    Optional<FollowEntity> findByFollowerIdAndFolloweeId(Long followerId, Long followeeId);

    @Query("SELECT f FROM FollowEntity f WHERE f.followerId = :followerId AND f.followeeId = :followeeId AND f.deletedAt IS NOT NULL")
    Optional<FollowEntity> findDeletedByFollowerIdAndFolloweeId(
            @Param("followerId") Long followerId, @Param("followeeId") Long followeeId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE FollowEntity f SET f.deletedAt = null, f.followStatus = :status WHERE f.id = :id")
    void restoreFollow(@Param("id") UUID id, @Param("status") FollowStatus status);

    Page<FollowEntity> findAllByFollowerIdAndFollowStatus(Long followerId,
            FollowStatus followStatus, Pageable pageable);

    Page<FollowEntity> findAllByFolloweeIdAndFollowStatus(Long followeeId,
            FollowStatus followStatus, Pageable pageable);

    long countByFolloweeIdAndFollowStatus(Long followeeId, FollowStatus followStatus);

    long countByFollowerIdAndFollowStatus(Long followerId, FollowStatus followStatus);

    boolean existsByFollowerIdAndFolloweeIdAndFollowStatus(Long followerId, Long followeeId,
            FollowStatus followStatus);
}
