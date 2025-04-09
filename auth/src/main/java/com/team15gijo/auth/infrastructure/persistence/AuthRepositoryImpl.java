package com.team15gijo.auth.infrastructure.persistence;

import com.team15gijo.auth.domain.model.AuthEntity;
import com.team15gijo.auth.domain.repository.AuthRepository;
import com.team15gijo.auth.infrastructure.persistence.jpa.AuthJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AuthRepositoryImpl implements AuthRepository {

    private final AuthJpaRepository authJpaRepository;

    @Override
    public void save(AuthEntity createdAuth) {
        authJpaRepository.save(createdAuth);
    }
}
