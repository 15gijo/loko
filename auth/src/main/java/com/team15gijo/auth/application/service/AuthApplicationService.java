package com.team15gijo.auth.application.service;

import com.team15gijo.auth.infrastructure.dto.v1.internal.AuthSignUpRequestDto;
import com.team15gijo.auth.infrastructure.dto.v1.internal.AuthSignUpResponseDto;
import com.team15gijo.auth.presentation.dto.v1.AuthLoginRequestDto;
import jakarta.validation.Valid;

public interface AuthApplicationService {

    AuthSignUpResponseDto signUp(AuthSignUpRequestDto authSignUpRequestDto);

    void login(@Valid AuthLoginRequestDto authLoginRequestDto);
}
