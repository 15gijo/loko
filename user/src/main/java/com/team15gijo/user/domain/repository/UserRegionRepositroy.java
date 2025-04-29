package com.team15gijo.user.domain.repository;

import com.team15gijo.user.domain.model.UserRegionEntity;
import java.util.Optional;
import java.util.UUID;

public interface UserRegionRepositroy {

    UserRegionEntity save(UserRegionEntity userRegionEntity);

    Optional<UserRegionEntity> findById(UUID regionId);
}
