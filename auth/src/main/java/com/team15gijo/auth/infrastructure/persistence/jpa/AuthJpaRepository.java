package com.team15gijo.auth.infrastructure.persistence.jpa;

import com.team15gijo.auth.domain.model.AuthEntity;
import com.team15gijo.auth.domain.model.LoginType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface AuthJpaRepository extends JpaRepository<AuthEntity, UUID> {

    Optional<AuthEntity> findByIdentifier(String identifier);

    boolean existsByIdentifierAndLoginType(String identifier, LoginType loginType);

    @Modifying
    @Query("""
                  UPDATE AuthEntity a
                  SET a.userId = :userId, a.createdBy = :userId
                  WHERE a.id = :authId
                  AND a.userId IS NULL
            """)
    void updateUserMeta(Long userId, UUID authId);

    Optional<AuthEntity> findByUserId(Long userId);

    Optional<AuthEntity> findByLoginTypeAndOauthId(LoginType loginType, String oauthId);
}
