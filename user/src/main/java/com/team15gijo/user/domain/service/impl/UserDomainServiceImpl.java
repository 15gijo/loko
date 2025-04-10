package com.team15gijo.user.domain.service.impl;

import com.team15gijo.common.exception.CustomException;
import com.team15gijo.user.domain.exception.UserDomainExceptionCode;
import com.team15gijo.user.domain.model.UserEntity;
import com.team15gijo.user.domain.model.UserStatus;
import com.team15gijo.user.domain.repository.UserRepository;
import com.team15gijo.user.domain.service.UserDomainService;
import com.team15gijo.user.presentation.dto.v1.UserSignUpRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDomainServiceImpl implements UserDomainService {

    private final UserRepository userRepository;

    @Override
    public UserEntity createUser(UserSignUpRequestDto userSignUpRequestDto) {
        //중복 회원 체크
        if (userRepository.existsByEmail(userSignUpRequestDto.email())) {
            throw new CustomException(UserDomainExceptionCode.DUPLICATED_USER_EMAIL);
        }
        if (userRepository.existsByNickName(userSignUpRequestDto.nickName())) {
            throw new CustomException(UserDomainExceptionCode.DUPLICATED_USER_NICKNAME);
        }

        return UserEntity.builder()
                .email(userSignUpRequestDto.email())
                .nickName(userSignUpRequestDto.nickName())
                .userName(userSignUpRequestDto.userName())
                .profile(userSignUpRequestDto.profile())
                .region(userSignUpRequestDto.region())
                .status(UserStatus.ACTIVE)
                .build();
    }
}
