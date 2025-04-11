package com.team15gijo.auth.domain.service;

import com.team15gijo.auth.application.dto.v1.AuthLoginRequestCommand;
import com.team15gijo.auth.domain.model.AuthEntity;
import com.team15gijo.auth.infrastructure.dto.v1.internal.AuthSignUpRequestDto;

public interface AuthDomainService {

    AuthEntity createAuth(AuthSignUpRequestDto authSignUpRequestDto);

    void loginAuth(AuthLoginRequestCommand authLoginRequestCommand);
}
