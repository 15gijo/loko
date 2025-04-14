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

    @Override
    public Optional<UserEntity> findById(Long userId) {
        return userJpaRepository.findById(userId);
    }

    //내부 통신
    @Override
    public Optional<String> findEmailById(Long userId) {
        return userJpaRepository.findEmailById(userId);
    }

    @Override
    public void updateCreatedBy(Long id) {
        userJpaRepository.updateCreatedById(id);
    }

    //내부 통신
    @Override
    public Optional<UserEntity> findByEmail(String identifier) {
        return userJpaRepository.findByEmail(identifier);
    }

    //내부 통신
    @Override
    public Optional<Long> findIdByNickName(String nickname) {
        return userJpaRepository.findIdByNickName(nickname);
    }

}
