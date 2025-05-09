package com.team15gijo.auth.application.service.v1;

import com.team15gijo.auth.application.dto.v1.AuthSignUpRequestCommand;
import com.team15gijo.auth.application.dto.v1.AuthValidatePasswordRequestCommand;
import com.team15gijo.auth.application.service.AuthApplicationService;
import com.team15gijo.auth.domain.exception.AuthDomainExceptionCode;
import com.team15gijo.auth.domain.model.AuthEntity;
import com.team15gijo.auth.domain.model.Role;
import com.team15gijo.auth.domain.repository.AuthRepository;
import com.team15gijo.auth.domain.service.AuthDomainService;
import com.team15gijo.auth.infrastructure.client.UserServiceClient;
import com.team15gijo.auth.infrastructure.dto.security.AuthLoginResponseCommand;
import com.team15gijo.auth.infrastructure.exception.AuthInfraExceptionCode;
import com.team15gijo.auth.infrastructure.jwt.JwtAdminProvider;
import com.team15gijo.auth.infrastructure.jwt.JwtProvider;
import com.team15gijo.auth.infrastructure.redis.repository.BlacklistRedisRepositoryImpl;
import com.team15gijo.auth.infrastructure.redis.repository.RefreshTokenRedisRepositoryImpl;
import com.team15gijo.auth.presentation.dto.internal.request.v1.AuthIdentifierUpdateRequestDto;
import com.team15gijo.auth.presentation.dto.internal.request.v1.AuthPasswordUpdateRequestDto;
import com.team15gijo.auth.presentation.dto.internal.request.v1.AuthSignUpRequestDto;
import com.team15gijo.auth.presentation.dto.internal.request.v1.AuthSignUpUpdateUserIdRequestDto;
import com.team15gijo.auth.presentation.dto.internal.request.v1.OAuthAuthSignUpRequestDto;
import com.team15gijo.auth.presentation.dto.request.v1.AdminAssignManagerRequestDto;
import com.team15gijo.auth.presentation.dto.request.v1.AssignAdminRequestDto;
import com.team15gijo.auth.presentation.dto.response.v2.AuthRefreshResponseDto;
import com.team15gijo.common.exception.CommonExceptionCode;
import com.team15gijo.common.exception.CustomException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Date;
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
    private final JwtProvider jwtProvider;
    private final RefreshTokenRedisRepositoryImpl refreshTokenRedisRepository;
    private final BlacklistRedisRepositoryImpl blacklistRedisRepository;

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
    public UUID signUpOAuth(OAuthAuthSignUpRequestDto oAuthAuthSignUpRequestDto) {
        AuthEntity createdAuth = authDomainService.createAuthOAuth(oAuthAuthSignUpRequestDto);
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

    @Override
    @Transactional
    public AuthRefreshResponseDto refresh(HttpServletRequest request,
            HttpServletResponse response) {

        //refresh 토큰 꺼내기
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refresh_token".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        if (refreshToken == null) {
            throw new CustomException(AuthInfraExceptionCode.REFRESH_TOKEN_EXPIRED);
        }

        //refresh 토큰 유효성 검사
        jwtProvider.validateRefreshToken(refreshToken);

        //refresh 토큰 userId 뽑기
        Claims claims = jwtProvider.parseToken(refreshToken);

        Long userId = Long.valueOf(claims.getSubject());
        String role = claims.get("role", String.class);
        String nickname = claims.get("nickname", String.class);
        String region = claims.get("region", String.class);

        //Redis 와 비교
        String redisRefreshToken = refreshTokenRedisRepository.get(userId)
                .orElseThrow(
                        () -> new CustomException(AuthInfraExceptionCode.REFRESH_TOKEN_EXPIRED));
        if (!redisRefreshToken.equals(refreshToken)) {
            throw new CustomException(AuthInfraExceptionCode.INVALID_REFRESH_TOKEN);
        }

        //accessToken 재발급
        AuthLoginResponseCommand authLoginResponseCommand = new AuthLoginResponseCommand(
                userId,
                role,
                nickname,
                region
        );
        String newAccessToken = jwtProvider.generateAccessToken(authLoginResponseCommand);

        return new AuthRefreshResponseDto(newAccessToken);
    }

    @Override
    @Transactional
    public void logout(String accessToken, Long userId) {
        //accessToken 남은 만료 시간 구하기
        Claims claims = jwtProvider.parseToken(accessToken);
        Date expirationDate = claims.getExpiration();
        long now = System.currentTimeMillis();
        long ttl = expirationDate.getTime() - now;

        //블랙리스트 accessToken 저장
        if (ttl > 0) {
            blacklistRedisRepository.save(accessToken, "logout", ttl);
        }

        //refresh 토큰 삭제
        refreshTokenRedisRepository.delete(userId);
    }

}
