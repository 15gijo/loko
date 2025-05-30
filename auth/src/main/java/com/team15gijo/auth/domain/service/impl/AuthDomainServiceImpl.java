package com.team15gijo.auth.domain.service.impl;

import com.team15gijo.auth.application.dto.v1.AuthValidatePasswordRequestCommand;
import com.team15gijo.auth.domain.exception.AuthDomainExceptionCode;
import com.team15gijo.auth.domain.model.AuthEntity;
import com.team15gijo.auth.domain.model.LoginType;
import com.team15gijo.auth.domain.model.Role;
import com.team15gijo.auth.domain.repository.AuthRepository;
import com.team15gijo.auth.domain.service.AuthDomainService;
import com.team15gijo.auth.application.dto.v1.AuthSignUpRequestCommand;
import com.team15gijo.auth.presentation.dto.internal.request.v1.OAuthAuthSignUpRequestDto;
import com.team15gijo.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthDomainServiceImpl implements AuthDomainService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthEntity createAuth(AuthSignUpRequestCommand authSignUpRequestCommand) {
        //중복 확인
        boolean isExists = authRepository.existsByIdentifierAndLoginType(
                authSignUpRequestCommand.getIdentifier(), authSignUpRequestCommand.getLoginType());
        if (isExists) {
            throw new CustomException(AuthDomainExceptionCode.AUTH_IS_DUPLICATED);
        }

        //비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(authSignUpRequestCommand.getPassword());

        //로그인 타입 및 인증 엔티티 생성
        AuthEntity createdAuth = AuthEntity.builder()
                .nickname(authSignUpRequestCommand.getNickname())
                .password(encodedPassword)
                .identifier(authSignUpRequestCommand.getIdentifier())
                .loginType(authSignUpRequestCommand.getLoginType())
                .role(Role.USER)
                .build();

        return createdAuth;
    }

    @Override
    public AuthEntity createAuthOAuth(OAuthAuthSignUpRequestDto oAuthAuthSignUpRequestDto) {
        //로그인 타입 및 인증 엔티티 생성
        AuthEntity createdAuthOAuth = AuthEntity.builder()
                .nickname(oAuthAuthSignUpRequestDto.getNickname())
                .password(oAuthAuthSignUpRequestDto.getPassword())
                .identifier(oAuthAuthSignUpRequestDto.getIdentifier())
                .loginType(LoginType.valueOf(oAuthAuthSignUpRequestDto.getLoginTypeName()))
                .role(Role.USER)
                .oauthId(oAuthAuthSignUpRequestDto.getOauthId())
                .build();

        return createdAuthOAuth;
    }

    @Override
    public void validatePassword(
            AuthValidatePasswordRequestCommand authValidatePasswordRequestCommand) {
        //비밀번호 비교
        if (!passwordEncoder.matches(
                authValidatePasswordRequestCommand.rawPassword(),
                authValidatePasswordRequestCommand.encodedPassword())) {
            throw new CustomException(AuthDomainExceptionCode.INVALID_PASSWORD);
        }
    }

}
