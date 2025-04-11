package com.team15gijo.auth.domain.repository;

import com.team15gijo.auth.domain.model.AuthEntity;
import java.util.Optional;

public interface AuthRepository {

    void save(AuthEntity createdAuth);

    Optional<AuthEntity> findByIdentifier(String identifier);
}
