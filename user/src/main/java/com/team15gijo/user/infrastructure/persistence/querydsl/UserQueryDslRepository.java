package com.team15gijo.user.infrastructure.persistence.querydsl;

import com.team15gijo.user.domain.model.UserEntity;
import java.time.LocalDateTime;
import java.util.List;

public interface UserQueryDslRepository {

    List<UserEntity> searchUsers(String keyword, Long userId, String nickname, String region, Long lastUserId, int size);
}
