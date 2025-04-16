package com.team15gijo.auth.domain.repository;

import com.team15gijo.auth.domain.model.AuthEntity;
import com.team15gijo.auth.domain.model.LoginType;
import java.util.Optional;
import java.util.UUID;

public interface AuthRepository {

    void save(AuthEntity createdAuth);

    Optional<AuthEntity> findByIdentifier(String identifier);

    boolean existsByIdentifierAndLoginType(String identifier, LoginType loginType);

    void updateUserMeta(Long userId, UUID authId);

    Optional<AuthEntity> findByUserId(Long userId);
}
