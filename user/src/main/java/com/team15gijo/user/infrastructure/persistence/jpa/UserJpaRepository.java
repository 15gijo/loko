package com.team15gijo.user.infrastructure.persistence.jpa;

import com.team15gijo.user.domain.model.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByNickName(String nickName);

    boolean existsByEmail(String email);

    Optional<UserEntity> findByEmail(String identifier);

    Optional<Long> findIdByNickName(String nickName);

    @Query("SELECT u.email FROM UserEntity u WHERE u.id = :id")
    Optional<String> findEmailById(Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update UserEntity u set u.createdBy = :id where u.id = :id")
    void updateCreatedById(Long id);
}
