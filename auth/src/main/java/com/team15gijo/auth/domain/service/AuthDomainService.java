package com.team15gijo.auth.domain.service;

import com.team15gijo.auth.application.dto.v1.AuthValidatePasswordRequestCommand;
import com.team15gijo.auth.domain.model.AuthEntity;
import com.team15gijo.auth.application.dto.v1.AuthSignUpRequestCommand;
import com.team15gijo.auth.presentation.dto.internal.request.v1.OAuthAuthSignUpRequestDto;

public interface AuthDomainService {

    AuthEntity createAuth(AuthSignUpRequestCommand authSignUpRequestCommand);

    void validatePassword(AuthValidatePasswordRequestCommand authValidatePasswordRequestCommand);

    AuthEntity createAuthOAuth(OAuthAuthSignUpRequestDto oAuthAuthSignUpRequestDto);
}
