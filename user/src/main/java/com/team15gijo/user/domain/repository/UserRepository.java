package com.team15gijo.user.domain.repository;

import com.team15gijo.user.application.dto.v1.AdminUserSearchCommand;
import com.team15gijo.user.domain.model.UserEntity;
import com.team15gijo.user.presentation.dto.v1.AdminUserReadResponseDto;
import com.team15gijo.user.presentation.dto.v1.UserReadsResponseDto;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepository {

    boolean existsByEmail(String email);

    boolean existsByNickName(String nickName);

    UserEntity save(UserEntity createdUser);

    Optional<UserEntity> findByEmail(String identifier);

    Optional<Long> findIdByNickName(String nickname);

    Optional<UserEntity> findById(Long userId);

    Optional<String> findEmailById(Long userId);

    void updateCreatedBy(Long id);

    Page<AdminUserReadResponseDto> searchUsersForAdmin(AdminUserSearchCommand adminUserSearchCommand, Pageable pageable);

    Optional<UserEntity> findByNickName(String nickname);

    Page<UserReadsResponseDto> searchUsers(String nickname, String username, String region, Pageable validatedPageable);
}
