package com.team15gijo.auth.application.service;

import com.team15gijo.auth.infrastructure.dto.v1.internal.AdminAssignManagerRequestDto;
import com.team15gijo.auth.infrastructure.dto.v1.internal.AuthIdentifierUpdateRequestDto;
import com.team15gijo.auth.infrastructure.dto.v1.internal.AuthPasswordUpdateRequestDto;
import com.team15gijo.auth.infrastructure.dto.v1.internal.AuthSignUpRequestDto;
import com.team15gijo.auth.infrastructure.dto.v1.internal.AuthSignUpUpdateUserIdRequestDto;
import com.team15gijo.auth.presentation.dto.v1.AssignAdminRequestDto;
import jakarta.validation.Valid;
import java.util.UUID;

public interface AuthApplicationService {

    UUID signUp(AuthSignUpRequestDto authSignUpRequestDto);

    void assignAdmin(String token, @Valid AssignAdminRequestDto assignAdminRequestDto);

    void signUpUpdateUserId(AuthSignUpUpdateUserIdRequestDto authSignUpUpdateUserIdRequestDto);

    void updateIdentifier(AuthIdentifierUpdateRequestDto authIdentifierUpdateRequestDto);

    void updatePassword(AuthPasswordUpdateRequestDto authPasswordUpdateRequestDto);

    void assignManger(@Valid AdminAssignManagerRequestDto adminAssignManagerRequestDto);
}
