package com.team15gijo.auth.infrastructure.persistence;

import com.team15gijo.auth.domain.model.AuthEntity;
import com.team15gijo.auth.domain.model.LoginType;
import com.team15gijo.auth.domain.repository.AuthRepository;
import com.team15gijo.auth.infrastructure.persistence.jpa.AuthJpaRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AuthRepositoryImpl implements AuthRepository {

    private final AuthJpaRepository authJpaRepository;

    //전부 내부 통신
    @Override
    public void save(AuthEntity createdAuth) {
        authJpaRepository.save(createdAuth);
    }

    @Override
    public Optional<AuthEntity> findByIdentifier(String identifier) {
        return authJpaRepository.findByIdentifier(identifier);
    }

    @Override
    public boolean existsByIdentifierAndLoginType(String identifier, LoginType loginType) {
        return authJpaRepository.existsByIdentifierAndLoginType(identifier, loginType);
    }

    @Override
    public void updateUserMeta(Long userId, UUID authId) {
        authJpaRepository.updateUserMeta(userId, authId);
    }
}
