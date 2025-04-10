package com.team15gijo.auth.domain.service.impl;

import com.team15gijo.auth.domain.model.AuthEntity;
import com.team15gijo.auth.domain.model.LoginType;
import com.team15gijo.auth.domain.model.Role;
import com.team15gijo.auth.domain.repository.AuthRepository;
import com.team15gijo.auth.domain.service.AuthDomainService;
import com.team15gijo.auth.infrastructure.dto.v1.internal.AuthSignUpRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthDomainServiceImpl implements AuthDomainService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthEntity createAuth(AuthSignUpRequestDto authSignUpRequestDto) {
        //비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(authSignUpRequestDto.getPassword());

        //로그인 타입 및 인증 엔티티 생성
        AuthEntity createdAuth = AuthEntity.builder()
                .nickname(authSignUpRequestDto.getNickname())
                .password(encodedPassword)
                .identifier(authSignUpRequestDto.getIdentifier())
                .loginType(LoginType.fromLoginTypeName(authSignUpRequestDto.getLoginTypeName()))
                .role(Role.USER)
                .build();

        return createdAuth;
    }
}
