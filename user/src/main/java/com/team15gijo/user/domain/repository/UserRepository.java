package com.team15gijo.user.domain.repository;

import com.team15gijo.user.domain.model.UserEntity;
import java.util.Optional;

public interface UserRepository {

    boolean existsByEmail(String email);

    boolean existsByNickName(String nickName);

    UserEntity save(UserEntity createdUser);

    Optional<UserEntity> findByEmail(String identifier);
}
