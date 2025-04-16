package com.team15gijo.user.infrastructure.persistence.jpa;

import com.team15gijo.user.domain.model.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByNickname(String nickname);

    boolean existsByEmail(String email);

    Optional<UserEntity> findByEmail(String identifier);

    @Query("SELECT u.id FROM UserEntity u WHERE u.nickname = :nickname")
    Optional<Long> findIdByNickname(String nickname);

    @Query("SELECT u.email FROM UserEntity u WHERE u.id = :id")
    Optional<String> findEmailById(Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update UserEntity u set u.createdBy = :id where u.id = :id")
    void updateCreatedById(Long id);

    Optional<UserEntity> findByNickname(String nickname);
}
