package com.team15gijo.auth.application.service;

import com.team15gijo.auth.infrastructure.dto.v1.internal.AuthSignUpRequestDto;
import com.team15gijo.auth.infrastructure.dto.v1.internal.AuthSignUpResponseDto;

public interface AuthApplicationService {

    AuthSignUpResponseDto signUp(AuthSignUpRequestDto authSignUpRequestDto);
}
