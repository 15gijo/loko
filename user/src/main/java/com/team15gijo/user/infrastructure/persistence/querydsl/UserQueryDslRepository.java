package com.team15gijo.user.infrastructure.persistence.querydsl;

import com.team15gijo.user.application.dto.v1.AdminUserSearchCommand;
import com.team15gijo.user.domain.model.UserEntity;
import com.team15gijo.user.presentation.dto.v1.AdminUserReadResponseDto;
import com.team15gijo.user.presentation.dto.v1.UserReadsResponseDto;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserQueryDslRepository {

    List<UserEntity> searchUsers(String keyword, Long userId, String nickname, String region,
            Long lastUserId, int size);

    Page<AdminUserReadResponseDto> searchUsersForAdmin(
            AdminUserSearchCommand adminUserSearchCommand, Pageable pageable);

    Page<UserReadsResponseDto> searchUsersForUser(String nickname, String username, String region,
            Pageable pageable);
}
