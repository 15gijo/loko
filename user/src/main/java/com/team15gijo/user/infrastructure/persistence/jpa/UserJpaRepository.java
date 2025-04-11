package com.team15gijo.user.infrastructure.persistence.jpa;

import com.team15gijo.user.domain.model.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByNickName(String nickName);

    boolean existsByEmail(String email);

    Optional<UserEntity> findByEmail(String identifier);
}
