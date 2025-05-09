package com.team15gijo.user.domain.service.impl;

import com.team15gijo.common.exception.CustomException;
import com.team15gijo.user.application.dto.v2.OAuthUserProfile;
import com.team15gijo.user.domain.exception.UserDomainExceptionCode;
import com.team15gijo.user.domain.model.UserEntity;
import com.team15gijo.user.domain.model.UserRegionEntity;
import com.team15gijo.user.domain.model.UserStatus;
import com.team15gijo.user.domain.repository.UserRepository;
import com.team15gijo.user.domain.service.UserDomainService;
import com.team15gijo.user.presentation.dto.request.v1.UserSignUpRequestDto;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDomainServiceImpl implements UserDomainService {

    private final UserRepository userRepository;

    @Override
    public UserEntity createUser(UserSignUpRequestDto userSignUpRequestDto,
            UserRegionEntity userRegionEntity) {
        //중복 회원 체크
        if (userRepository.existsByEmail(userSignUpRequestDto.email())) {
            throw new CustomException(UserDomainExceptionCode.DUPLICATED_USER_EMAIL);
        }
        if (userRepository.existsByNickname(userSignUpRequestDto.nickname())) {
            throw new CustomException(UserDomainExceptionCode.DUPLICATED_USER_NICKNAME);
        }

        return UserEntity.builder()
                .email(userSignUpRequestDto.email())
                .nickname(userSignUpRequestDto.nickname())
                .username(userSignUpRequestDto.username())
                .profile(userSignUpRequestDto.profile())
                .region(userSignUpRequestDto.region())
                .status(UserStatus.ACTIVE)
                .regionId(userRegionEntity)
                .build();
    }

    @Override
    public UserEntity createUserOauth(
            OAuthUserProfile oAuthUserProfile,
            String username,
            String regionName,
            UserRegionEntity userRegionEntity) {
        String nickname = oAuthUserProfile.getNickname();
        if (nickname == null || nickname.isBlank()) {
            nickname = "guest_" + UUID.randomUUID().toString().substring(0, 8);
        }

        if (username == null || username.isBlank()) {
            username = nickname;
        }
        return UserEntity.builder()
                .email(oAuthUserProfile.getEmail())
                .nickname(nickname)
                .username(username)
                .profile(oAuthUserProfile.getProfileImageUrl())
                .region(regionName)
                .status(UserStatus.ACTIVE)
                .regionId(userRegionEntity)
                .build();
    }
}
