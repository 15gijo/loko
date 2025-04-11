package com.team15gijo.auth.infrastructure.persistence.jpa;

import com.team15gijo.auth.domain.model.AuthEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthJpaRepository extends JpaRepository<AuthEntity, UUID> {

    Optional<AuthEntity> findByIdentifier(String identifier);
}
