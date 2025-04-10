package com.team15gijo.user.domain.service;

import com.team15gijo.user.domain.model.UserEntity;
import com.team15gijo.user.presentation.dto.v1.UserSignUpRequestDto;

public interface UserDomainService {

    UserEntity createUser(UserSignUpRequestDto userSignUpRequestDto);
}
