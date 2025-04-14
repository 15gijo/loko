package com.team15gijo.auth.application.service;

import com.team15gijo.auth.application.dto.v1.AuthLoginResponseCommand;
import com.team15gijo.auth.infrastructure.dto.v1.internal.AuthSignUpRequestDto;
import com.team15gijo.auth.infrastructure.dto.v1.internal.AuthSignUpUpdateUserIdRequestDto;
import com.team15gijo.auth.presentation.dto.v1.AssignAdminRequestDto;
import com.team15gijo.auth.presentation.dto.v1.AuthLoginRequestDto;
import jakarta.validation.Valid;
import java.util.UUID;

public interface AuthApplicationService {

    UUID signUp(AuthSignUpRequestDto authSignUpRequestDto);

    AuthLoginResponseCommand login(@Valid AuthLoginRequestDto authLoginRequestDto);

    void assignAdmin(String token, @Valid AssignAdminRequestDto assignAdminRequestDto);

    void signUpUpdateUserId(AuthSignUpUpdateUserIdRequestDto authSignUpUpdateUserIdRequestDto);
}
