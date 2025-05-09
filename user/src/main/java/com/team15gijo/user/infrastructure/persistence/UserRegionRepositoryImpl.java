package com.team15gijo.user.infrastructure.persistence;

import com.team15gijo.user.domain.model.UserRegionEntity;
import com.team15gijo.user.domain.repository.UserRegionRepositroy;
import com.team15gijo.user.infrastructure.persistence.jpa.UserRegionJpaRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRegionRepositoryImpl implements UserRegionRepositroy {

    private final UserRegionJpaRepository userRegionJpaRepository;

    @Override
    public UserRegionEntity save(UserRegionEntity userRegionEntity) {
        userRegionJpaRepository.save(userRegionEntity);
        return userRegionEntity;
    }

    @Override
    public Optional<UserRegionEntity> findById(UUID regionId) {
        return userRegionJpaRepository.findById(regionId);
    }

}
