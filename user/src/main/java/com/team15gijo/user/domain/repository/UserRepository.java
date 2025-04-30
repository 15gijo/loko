package com.team15gijo.user.domain.repository;

import com.team15gijo.user.application.dto.v1.AdminUserSearchCommand;
import com.team15gijo.user.domain.model.UserEntity;
import com.team15gijo.user.presentation.dto.internal.response.v1.UserAndRegionInfoFollowResponseDto;
import com.team15gijo.user.presentation.dto.v1.AdminUserReadResponseDto;
import com.team15gijo.user.presentation.dto.v1.UserReadsResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepository {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    UserEntity save(UserEntity createdUser);

    Optional<UserEntity> findByEmail(String identifier);

    Optional<Long> findIdByNickname(String nickname);

    Optional<UserEntity> findById(Long userId);

    Optional<String> findEmailById(Long userId);

    void updateCreatedBy(Long id);

    Page<AdminUserReadResponseDto> searchUsersForAdmin(AdminUserSearchCommand adminUserSearchCommand, Pageable pageable);

    Optional<UserEntity> findByNickname(String nickname);

    Page<UserReadsResponseDto> searchUsers(String nickname, String username, String region, Pageable validatedPageable);

    void deleteById(Long userId);

    List<UserAndRegionInfoFollowResponseDto> findUserAndRegionInfos(Point location, List<Long> userIds);

    int incrementFollowingCount(Long followerId);

    int incrementFollowerCount(Long followeeId);

    int decrementFollowingCount(Long followerId);

    int decrementFollowerCount(Long followeeId);

    List<UserEntity> findAll();
}
