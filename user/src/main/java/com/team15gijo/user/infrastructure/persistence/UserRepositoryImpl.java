package com.team15gijo.user.infrastructure.persistence;

import com.team15gijo.user.application.dto.v1.AdminUserSearchCommand;
import com.team15gijo.user.domain.model.UserEntity;
import com.team15gijo.user.domain.repository.UserRepository;
import com.team15gijo.user.infrastructure.persistence.jpa.UserJpaRepository;
import com.team15gijo.user.infrastructure.persistence.querydsl.UserQueryDslRepository;
import com.team15gijo.user.presentation.dto.v1.AdminUserReadResponseDto;
import com.team15gijo.user.presentation.dto.v1.UserReadsResponseDto;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final UserQueryDslRepository userQueryDslRepository;


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

    @Override
    public Page<AdminUserReadResponseDto> searchUsersForAdmin(
            AdminUserSearchCommand adminUserSearchCommand, Pageable pageable) {
        return userQueryDslRepository.searchUsersForAdmin(adminUserSearchCommand, pageable);
    }

    @Override
    public Optional<UserEntity> findByNickName(String nickname) {
        return userJpaRepository.findByNickName(nickname);
    }

    @Override
    public Page<UserReadsResponseDto> searchUsers(String nickname, String username, String region,
            Pageable validatedPageable) {
        return userQueryDslRepository.searchUsersForUser(nickname, username, region, validatedPageable);
    }

    //내부 통신
    @Override
    public Optional<String> findEmailById(Long userId) {
        return userJpaRepository.findEmailById(userId);
    }

    //내부통신
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
