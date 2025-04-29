package com.team15gijo.user.infrastructure.persistence.jpa;

import com.team15gijo.user.domain.model.UserEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByNickname(String nickname);

    boolean existsByEmail(String email);

    Optional<UserEntity> findByEmail(String identifier);

    @Query("SELECT u.id FROM UserEntity u WHERE u.nickname = :nickname")
    Optional<Long> findIdByNickname(String nickname);

    @Query("SELECT u.email FROM UserEntity u WHERE u.id = :id")
    Optional<String> findEmailById(Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update UserEntity u set u.createdBy = :id where u.id = :id")
    void updateCreatedById(Long id);

    Optional<UserEntity> findByNickname(String nickname);

    @Modifying
    @Query("UPDATE UserEntity u SET u.followingCount = u.followingCount + 1 WHERE u.id = :userId")
    void incrementFollowingCount(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE UserEntity u SET u.followerCount = u.followerCount + 1 WHERE u.id = :userId")
    void incrementFollowerCount(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE UserEntity u SET u.followingCount = u.followingCount - 1 WHERE u.id = :userId")
    void decrementFollowingCount(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE UserEntity u SET u.followerCount = u.followerCount - 1 WHERE u.id = :userId")
    void decrementFollowerCount(@Param("userId") Long userId);
}
