package com.team15gijo.auth.application.service.v1;

import com.team15gijo.auth.application.service.AuthApplicationService;
import com.team15gijo.auth.domain.model.AuthEntity;
import com.team15gijo.auth.domain.repository.AuthRepository;
import com.team15gijo.auth.domain.service.AuthDomainService;
import com.team15gijo.auth.infrastructure.dto.v1.internal.AuthSignUpRequestDto;
import com.team15gijo.auth.infrastructure.dto.v1.internal.AuthSignUpResponseDto;
import com.team15gijo.auth.presentation.dto.v1.AuthLoginRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthApplicationServiceImpl implements AuthApplicationService {

    private final AuthDomainService authDomainService;
    private final AuthRepository authRepository;

    @Override
    @Transactional
    public AuthSignUpResponseDto signUp(AuthSignUpRequestDto authSignUpRequestDto) {
        log.info("signup-auth authSignUpRequestDto={}", authSignUpRequestDto);
        AuthEntity createdAuth = authDomainService.createAuth(authSignUpRequestDto);
        authRepository.save(createdAuth);
        return new AuthSignUpResponseDto("회원 가입 성공", String.valueOf(HttpStatus.CREATED.value()));
    }

    @Override
    @Transactional
    public void login(AuthLoginRequestDto authLoginRequestDto) {
        log.info("login-auth authLoginRequestDto={}", authLoginRequestDto);
        AuthEntity loginAuth = authDomainService.loginAuth(authLoginRequestCommand);

    }
}
