package com.team15gijo.auth.domain.service;

import com.team15gijo.auth.application.dto.v1.AuthValidatePasswordRequestCommand;
import com.team15gijo.auth.domain.model.AuthEntity;
import com.team15gijo.auth.infrastructure.dto.v1.internal.AuthSignUpRequestCommand;

public interface AuthDomainService {

    AuthEntity createAuth(AuthSignUpRequestCommand authSignUpRequestCommand);

    void validatePassword(AuthValidatePasswordRequestCommand authValidatePasswordRequestCommand);
}
