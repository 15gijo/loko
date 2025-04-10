package com.team15gijo.auth.domain.repository;

import com.team15gijo.auth.domain.model.AuthEntity;

public interface AuthRepository {

    void save(AuthEntity createdAuth);
}
