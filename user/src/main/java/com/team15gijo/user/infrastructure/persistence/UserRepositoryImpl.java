package com.team15gijo.user.infrastructure.persistence;

import com.team15gijo.user.domain.model.UserEntity;
import com.team15gijo.user.domain.repository.UserRepository;
import com.team15gijo.user.infrastructure.persistence.jpa.UserJpaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;


    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByNickName(String nickName) {
        return userJpaRepository.existsByNickName(nickName);
    }

    @Override
    public UserEntity save(UserEntity createdUser) {
        return userJpaRepository.save(createdUser);
    }

    //내부 통신
    @Override
    public Optional<UserEntity> findByEmail(String identifier) {
        return userJpaRepository.findByEmail(identifier);
    }
}
