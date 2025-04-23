package com.team15gijo.auth.application.service;

import com.team15gijo.auth.presentation.dto.request.v1.AdminAssignManagerRequestDto;
import com.team15gijo.auth.presentation.dto.internal.request.v1.AuthIdentifierUpdateRequestDto;
import com.team15gijo.auth.presentation.dto.internal.request.v1.AuthPasswordUpdateRequestDto;
import com.team15gijo.auth.presentation.dto.internal.request.v1.AuthSignUpRequestDto;
import com.team15gijo.auth.presentation.dto.internal.request.v1.AuthSignUpUpdateUserIdRequestDto;
import com.team15gijo.auth.presentation.dto.request.v1.AssignAdminRequestDto;
import com.team15gijo.auth.presentation.dto.response.v2.AuthRefreshResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.UUID;

public interface AuthApplicationService {

    UUID signUp(AuthSignUpRequestDto authSignUpRequestDto);

    void assignAdmin(String token, @Valid AssignAdminRequestDto assignAdminRequestDto);

    void signUpUpdateUserId(AuthSignUpUpdateUserIdRequestDto authSignUpUpdateUserIdRequestDto);

    void updateIdentifier(AuthIdentifierUpdateRequestDto authIdentifierUpdateRequestDto);

    void updatePassword(AuthPasswordUpdateRequestDto authPasswordUpdateRequestDto);

    void assignManger(@Valid AdminAssignManagerRequestDto adminAssignManagerRequestDto);

    AuthRefreshResponseDto refresh(HttpServletRequest request, HttpServletResponse response);

    void logout(String accessToken, Long userId);
}
