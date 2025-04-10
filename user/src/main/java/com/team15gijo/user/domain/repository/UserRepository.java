package com.team15gijo.user.domain.repository;

import com.team15gijo.user.domain.model.UserEntity;

public interface UserRepository {

    boolean existsByEmail(String email);

    boolean existsByNickName(String nickName);

    UserEntity save(UserEntity createdUser);
}
