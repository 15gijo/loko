package com.team15gijo.user.domain.service;

import com.team15gijo.user.application.dto.v2.OAuthUserProfile;
import com.team15gijo.user.domain.model.UserEntity;
import com.team15gijo.user.domain.model.UserRegionEntity;
import com.team15gijo.user.presentation.dto.request.v1.UserSignUpRequestDto;

public interface UserDomainService {

    UserEntity createUser(UserSignUpRequestDto userSignUpRequestDto,
            UserRegionEntity userRegionEntity);

    UserEntity createUserOauth(
            OAuthUserProfile oAuthUserProfile,
            String username,
            String regionName,
            UserRegionEntity userRegionEntity);
}
