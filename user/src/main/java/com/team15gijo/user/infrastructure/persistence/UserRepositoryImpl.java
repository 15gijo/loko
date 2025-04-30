package com.team15gijo.user.infrastructure.persistence;

import com.team15gijo.user.application.dto.v1.AdminUserSearchCommand;
import com.team15gijo.user.domain.model.UserEntity;
import com.team15gijo.user.domain.repository.UserRepository;
import com.team15gijo.user.infrastructure.persistence.jpa.UserJpaRepository;
import com.team15gijo.user.infrastructure.persistence.querydsl.UserQueryDslRepository;
import com.team15gijo.user.presentation.dto.internal.response.v1.UserAndRegionInfoFollowResponseDto;
import com.team15gijo.user.presentation.dto.v1.AdminUserReadResponseDto;
import com.team15gijo.user.presentation.dto.v1.UserReadsResponseDto;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
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
    public boolean existsByNickname(String nickname) {
        return userJpaRepository.existsByNickname(nickname);
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
    public Optional<UserEntity> findByNickname(String nickname) {
        return userJpaRepository.findByNickname(nickname);
    }

    @Override
    public Page<UserReadsResponseDto> searchUsers(String nickname, String username, String region,
            Pageable validatedPageable) {
        return userQueryDslRepository.searchUsersForUser(nickname, username, region,
                validatedPageable);
    }

    @Override
    public void deleteById(Long userId) {
        userJpaRepository.deleteById(userId);
    }

    //내부 통신
    @Override
    public List<UserAndRegionInfoFollowResponseDto> findUserAndRegionInfos(Point location, List<Long> userIds) {
        return userQueryDslRepository.findUserAndRegionInfos(location, userIds);
    }


    //내부 통신
    @Override
    public Optional<String> findEmailById(Long userId) {
        return userJpaRepository.findEmailById(userId);
    }

    //내부 통신
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
    public Optional<Long> findIdByNickname(String nickname) {
        return userJpaRepository.findIdByNickname(nickname);
    }

    //내부 통신 - 카프카
    @Override
    public int incrementFollowingCount(Long followerId) {
        return userJpaRepository.incrementFollowingCount(followerId);
    }

    //내부 통신 - 카프카
    @Override
    public int incrementFollowerCount(Long followeeId) {
        return userJpaRepository.incrementFollowerCount(followeeId);
    }

    //내부 통신 - 카프카
    @Override
    public int decrementFollowingCount(Long followerId) {
        return userJpaRepository.decrementFollowingCount(followerId);
    }

    //내부 통신 - 카프카
    @Override
    public int decrementFollowerCount(Long followeeId) {
        return userJpaRepository.decrementFollowerCount(followeeId);
    }

    //내부 통신 - 레디스 리프레쉬
    @Override
    public List<UserEntity> findAll() {
        return userJpaRepository.findAll();
    }

}
