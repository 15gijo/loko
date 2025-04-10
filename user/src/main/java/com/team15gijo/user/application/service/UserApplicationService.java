package com.team15gijo.user.application.service;

import com.team15gijo.user.presentation.dto.v1.UserSignUpRequestDto;
import com.team15gijo.user.presentation.dto.v1.UserSignUpResponseDto;
import jakarta.validation.Valid;

public interface UserApplicationService {

    UserSignUpResponseDto createUser(@Valid UserSignUpRequestDto userSignUpRequestDto);
}
