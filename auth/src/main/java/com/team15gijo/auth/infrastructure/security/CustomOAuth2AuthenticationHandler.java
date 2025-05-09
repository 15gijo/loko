package com.team15gijo.auth.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team15gijo.auth.domain.model.AuthEntity;
import com.team15gijo.auth.domain.model.LoginType;
import com.team15gijo.auth.domain.repository.AuthRepository;
import com.team15gijo.auth.infrastructure.client.UserServiceClient;
import com.team15gijo.auth.infrastructure.dto.feign.response.v1.UserFeignInfoResponseDto;
import com.team15gijo.auth.infrastructure.dto.security.AuthLoginResponseCommand;
import com.team15gijo.auth.infrastructure.exception.AuthInfraExceptionCode;
import com.team15gijo.auth.infrastructure.jwt.JwtProvider;
import com.team15gijo.auth.infrastructure.redis.repository.AuthOAuthSignUpRedisRepositoryImpl;
import com.team15gijo.auth.infrastructure.redis.repository.RefreshTokenRedisRepositoryImpl;
import com.team15gijo.auth.infrastructure.security.dto.UserProfile;
import com.team15gijo.common.dto.ApiResponse;
import com.team15gijo.common.exception.CustomException;
import com.team15gijo.common.exception.ExceptionCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2AuthenticationHandler implements AuthenticationSuccessHandler,
        AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;
    private final AuthRepository authRepository;
    private final JwtProvider jwtProvider;
    private final UserServiceClient userServiceClient;
    private final RefreshTokenRedisRepositoryImpl refreshTokenRedisRepositoryImpl;
    private final AuthOAuthSignUpRedisRepositoryImpl oAuthSignUpRedisRepositoryImpl;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        log.error("[OAuth2] 로그인 실패: {}", exception.getMessage());

        ApiResponse<ExceptionCode> apiResponse = ApiResponse.exception(
                AuthInfraExceptionCode.UNAUTHORIZED_OAUTH2_LOGIN.getMessage(),
                AuthInfraExceptionCode.UNAUTHORIZED_OAUTH2_LOGIN);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = oauth2Token.getPrincipal();

        //provider 추출
        String provider = oauth2Token.getAuthorizedClientRegistrationId();

        //providerId, email 등 추출
        UserProfile userProfile = OAuthAttributes.extract(provider, oAuth2User.getAttributes());

        log.info("[OAuth2] 로그인 성공 - provider={}, userProfile={}", provider, userProfile);

        //회원 여부 조회
        LoginType loginType = LoginType.fromLoginTypeName(provider);
        AuthEntity authEntity = authRepository.findByLoginTypeAndOauthId(loginType,
                        userProfile.getProviderId())
//                .orElseThrow(() -> new CustomException(AuthInfraExceptionCode.OAUTH2_SIGNUP_REQUIRED));
                .orElse(null);
        log.info("authEntity={}", authEntity);
        try {
            //회원이 아닐때
            if (authEntity == null) {
                String oauthTempSignUpToken = jwtProvider.generateOAuthTempSignUpToken(userProfile);

                //임시 oauth 토큰 레디스에 저장
                oAuthSignUpRedisRepositoryImpl.save(
                        oauthTempSignUpToken,
                        String.valueOf(userProfile),
                        jwtProvider.getOauthTempSignUpTokenExpiration()
                );

                //응답 헤더
                response.setHeader("X-OAUTH-SIGNUP-TOKEN", oauthTempSignUpToken);

                ApiResponse<ExceptionCode> apiResponse = ApiResponse.exception(
                        AuthInfraExceptionCode.OAUTH2_SIGNUP_REQUIRED.getMessage(),
                        AuthInfraExceptionCode.OAUTH2_SIGNUP_REQUIRED);

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
                return;
            }

            //사용자 조회
            UserFeignInfoResponseDto userFeignInfoResponseDto = userServiceClient.getUserInfo(
                    userProfile.getEmail());

            if ("WITHDRAWN".equals(userFeignInfoResponseDto.getUserStatusName())) {
                throw new CustomException(AuthInfraExceptionCode.USER_ALREADY_WITHDRAWN);
            }

            //응답 커맨드
            AuthLoginResponseCommand loginUser = new AuthLoginResponseCommand(
                    userFeignInfoResponseDto.getUserId(),
                    authEntity.getRole().name(),
                    userFeignInfoResponseDto.getNickname(),
                    userFeignInfoResponseDto.getRegion());

            String accessToken = jwtProvider.generateAccessToken(loginUser);
            String refreshToken = jwtProvider.generateRefreshToken(loginUser);

            //refresh 토큰 레디스에 저장
            refreshTokenRedisRepositoryImpl.save(
                    loginUser.userId(),
                    refreshToken,
                    jwtProvider.getRefreshTokenExpiration()
            );

            //응답 헤더
            response.setHeader("Authorization", "Bearer " + accessToken);

            //refresh HttpOnly 쿠키에 세팅
            Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(false); //https 배포 시에 true로
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge((int) (jwtProvider.getRefreshTokenExpiration() / 1000));
            refreshTokenCookie.setAttribute("sameSite", "none"); //msa 이므로 다른 도메인 간 쿠키 보내기 허용
            response.addCookie(refreshTokenCookie);

            //공통 응답 처리
            ApiResponse<AuthLoginResponseCommand> apiResponse = ApiResponse.success("로그인 성공",
                    loginUser);
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(apiResponse));

        } catch (IOException e) {
            throw new CustomException(AuthInfraExceptionCode.JSON_WRITE_FAILED, e);
        }

    }


}
