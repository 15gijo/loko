package com.team15gijo.auth.application.service.v1;

import com.team15gijo.auth.application.dto.v1.AuthLoginRequestCommand;
import com.team15gijo.auth.application.dto.v1.AuthLoginResponseCommand;
import com.team15gijo.auth.application.service.AuthApplicationService;
import com.team15gijo.auth.domain.exception.AuthDomainExceptionCode;
import com.team15gijo.auth.domain.model.AuthEntity;
import com.team15gijo.auth.domain.model.LoginType;
import com.team15gijo.auth.domain.model.Role;
import com.team15gijo.auth.domain.repository.AuthRepository;
import com.team15gijo.auth.domain.service.AuthDomainService;
import com.team15gijo.auth.infrastructure.client.UserServiceClient;
import com.team15gijo.auth.infrastructure.dto.v1.UserFeignInfoResponseDto;
import com.team15gijo.auth.infrastructure.dto.v1.internal.AuthSignUpRequestCommand;
import com.team15gijo.auth.infrastructure.dto.v1.internal.AuthSignUpRequestDto;
import com.team15gijo.auth.infrastructure.dto.v1.internal.AuthSignUpUpdateUserIdRequestDto;
import com.team15gijo.auth.infrastructure.jwt.JwtAdminProvider;
import com.team15gijo.auth.presentation.dto.v1.AssignAdminRequestDto;
import com.team15gijo.auth.presentation.dto.v1.AuthLoginRequestDto;
import com.team15gijo.common.exception.CommonExceptionCode;
import com.team15gijo.common.exception.CustomException;
import io.jsonwebtoken.Claims;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthApplicationServiceImpl implements AuthApplicationService {

    private final AuthDomainService authDomainService;
    private final AuthRepository authRepository;
    private final UserServiceClient userServiceClient;
    private final JwtAdminProvider jwtAdminProvider;

    @Override
    @Transactional
    public UUID signUp(AuthSignUpRequestDto authSignUpRequestDto) {
        AuthSignUpRequestCommand authSignUpRequestCommand = AuthSignUpRequestCommand.from(
                authSignUpRequestDto);
        AuthEntity createdAuth = authDomainService.createAuth(authSignUpRequestCommand);
        authRepository.save(createdAuth);
        return createdAuth.getId();
    }

    @Override
    @Transactional
    public AuthLoginResponseCommand login(AuthLoginRequestDto authLoginRequestDto) {

        //인증 조회
        AuthEntity auth = authRepository.findByIdentifier(authLoginRequestDto.identifier())
                .orElseThrow(() -> new CustomException(
                        AuthDomainExceptionCode.USER_IDENTIFIER_NOT_FOUND));

        //사용자 조회
        UserFeignInfoResponseDto userFeignInfoResponseDto = userServiceClient.getUserInfo(
                auth.getIdentifier());

        //로그인 타입 정의
        AuthLoginRequestCommand authLoginRequestCommand = new AuthLoginRequestCommand(
                auth.getPassword(),
                authLoginRequestDto.password(),
                auth.getRole(),
                userFeignInfoResponseDto.getUserId(),
                userFeignInfoResponseDto.getNickname(),
                userFeignInfoResponseDto.getRegion(),
                LoginType.PASSWORD
        );

        //비번 검증 -> 추후 도메인 로직 확장
        authDomainService.loginAuth(authLoginRequestCommand);

        //인증 완료 유저 jwt 헤더에 넣을 내용 보내기
        return new AuthLoginResponseCommand(
                authLoginRequestCommand.userId(),
                authLoginRequestCommand.nickname(),
                authLoginRequestCommand.role().name(),
                authLoginRequestCommand.region()
        );
    }

    //어드민 권한 부여
    @Override
    @Transactional
    public void assignAdmin(String token, AssignAdminRequestDto assignAdminRequestDto) {

        //유저 검사, 이메일 가져오기
        String email = userServiceClient.getEmailByUserId(assignAdminRequestDto.userId());

        //어드민 토큰 파싱
        String rawToken = token.replace("Bearer ", "");
        System.out.println("rawToken = " + rawToken);
        Claims claims = jwtAdminProvider.parseToken(rawToken);
        System.out.println("🎯 subject: " + claims.getSubject());

        //trusted-admin 서브젝트 확인
        if (!"trusted-admin".equals(claims.getSubject())) {
            throw new CustomException(CommonExceptionCode.FORBIDDEN_ACCESS);
        }

        //인증 테이블 유저 확인 후 어드민 권한 부여
        AuthEntity auth = authRepository.findByIdentifier(email)
                .orElseThrow(() -> new CustomException(
                        AuthDomainExceptionCode.USER_IDENTIFIER_NOT_FOUND));

        auth.updateRole(Role.ADMIN);
    }

    @Override
    @Transactional
    public void signUpUpdateUserId(
            AuthSignUpUpdateUserIdRequestDto authSignUpUpdateUserIdRequestDto) {
        authRepository.updateUserMeta(authSignUpUpdateUserIdRequestDto.userId(),
                authSignUpUpdateUserIdRequestDto.authId());
    }
}
