package com.team15gijo.auth.application.service.v1;

import com.team15gijo.auth.application.dto.v1.AuthValidatePasswordRequestCommand;
import com.team15gijo.auth.application.service.AuthApplicationService;
import com.team15gijo.auth.domain.exception.AuthDomainExceptionCode;
import com.team15gijo.auth.domain.model.AuthEntity;
import com.team15gijo.auth.domain.model.Role;
import com.team15gijo.auth.domain.repository.AuthRepository;
import com.team15gijo.auth.domain.service.AuthDomainService;
import com.team15gijo.auth.infrastructure.client.UserServiceClient;
import com.team15gijo.auth.infrastructure.dto.v1.internal.AdminAssignManagerRequestDto;
import com.team15gijo.auth.infrastructure.dto.v1.internal.AuthIdentifierUpdateRequestDto;
import com.team15gijo.auth.infrastructure.dto.v1.internal.AuthPasswordUpdateRequestDto;
import com.team15gijo.auth.infrastructure.dto.v1.internal.AuthSignUpRequestCommand;
import com.team15gijo.auth.infrastructure.dto.v1.internal.AuthSignUpRequestDto;
import com.team15gijo.auth.infrastructure.dto.v1.internal.AuthSignUpUpdateUserIdRequestDto;
import com.team15gijo.auth.infrastructure.jwt.JwtAdminProvider;
import com.team15gijo.auth.presentation.dto.v1.AssignAdminRequestDto;
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

    @Override
    @Transactional
    public void updateIdentifier(AuthIdentifierUpdateRequestDto authIdentifierUpdateRequestDto) {
        //인증 디비 확인
        AuthEntity auth = authRepository.findByUserId(authIdentifierUpdateRequestDto.userId())
                .orElseThrow(() -> new CustomException(
                        AuthDomainExceptionCode.AUTH_NOT_FOUND));

        //비밀번호 확인
        authDomainService.validatePassword(
                new AuthValidatePasswordRequestCommand(
                        authIdentifierUpdateRequestDto.password(),
                        auth.getPassword()
                )
        );

        //업데이트
        auth.updateIdentifier(authIdentifierUpdateRequestDto.newIdentifier());
    }

    @Override
    @Transactional
    public void updatePassword(AuthPasswordUpdateRequestDto authPasswordUpdateRequestDto) {
        //인증 디비 확인
        AuthEntity auth = authRepository.findByUserId(authPasswordUpdateRequestDto.userId())
                .orElseThrow(() -> new CustomException(
                        AuthDomainExceptionCode.AUTH_NOT_FOUND));

        //비밀번호 확인
        authDomainService.validatePassword(
                new AuthValidatePasswordRequestCommand(
                        authPasswordUpdateRequestDto.currentPassword(),
                        auth.getPassword()
                )
        );

        //비밀번호 업데이트
        auth.updatePassword(authPasswordUpdateRequestDto.newPassword());
    }

    @Override
    @Transactional
    public void assignManger(AdminAssignManagerRequestDto adminAssignManagerRequestDto) {
        //인증 디비 확인
        AuthEntity auth = authRepository.findByUserId(adminAssignManagerRequestDto.targetUserId())
                .orElseThrow(() -> new CustomException(
                        AuthDomainExceptionCode.AUTH_NOT_FOUND));

        //USER롤 확인
        if (auth.getRole() != Role.USER) {
            throw new CustomException(AuthDomainExceptionCode.ALREADY_PROMOTED);
        }

        //롤 부여 업데이트
        auth.updateRole(Role.MANAGER);
    }
}
