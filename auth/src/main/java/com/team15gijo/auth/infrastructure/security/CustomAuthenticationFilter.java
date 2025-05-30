package com.team15gijo.auth.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team15gijo.auth.infrastructure.client.UserServiceClient;
import com.team15gijo.auth.infrastructure.dto.feign.response.v1.UserFeignInfoResponseDto;
import com.team15gijo.auth.infrastructure.dto.security.AuthLoginResponseCommand;
import com.team15gijo.auth.infrastructure.exception.AuthInfraExceptionCode;
import com.team15gijo.auth.infrastructure.jwt.JwtProvider;
import com.team15gijo.auth.infrastructure.redis.repository.RefreshTokenRedisRepositoryImpl;
import com.team15gijo.auth.presentation.dto.request.v1.AuthLoginRequestDto;
import com.team15gijo.common.dto.ApiResponse;
import com.team15gijo.common.exception.CustomException;
import com.team15gijo.common.exception.ExceptionCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final ObjectMapper objectMapper;
    private final JwtProvider jwtProvider;
    private final UserServiceClient userServiceClient;
    private final RefreshTokenRedisRepositoryImpl refreshTokenRedisRepositoryImpl;

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response) throws AuthenticationException {

        try {
            //요청 dto json 파싱
            AuthLoginRequestDto authLoginRequestDto = objectMapper.readValue(
                    request.getInputStream(), AuthLoginRequestDto.class);

            //인증 객체
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            authLoginRequestDto.identifier(),
                            authLoginRequestDto.password());

            return authenticationManager.authenticate(authToken);

        } catch (IOException e) {
            throw new CustomException(AuthInfraExceptionCode.LOGIN_REQUEST_PARSING_FAILED, e);
        }
    }

    //attemptAuthentication 인증 성공 로직
    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain,
            Authentication authResult
    ) {
        try {
            //Jwt 생성 및 출력
            log.info("인증 성공: {}", authResult.getPrincipal());

            String identifier = (String) authResult.getPrincipal();
            String role = (String) authResult.getAuthorities().stream()
                    .findFirst()
                    .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                    .orElse("USER");

            //사용자 조회
            UserFeignInfoResponseDto userFeignInfoResponseDto = userServiceClient.getUserInfo(
                    identifier);

            //사용자 상태 탈퇴 확인
            if ("WITHDRAWN".equals(userFeignInfoResponseDto.getUserStatusName())) {
                throw new CustomException(AuthInfraExceptionCode.USER_ALREADY_WITHDRAWN);
            }

            //응답 커맨드
            AuthLoginResponseCommand loginUser = new AuthLoginResponseCommand(
                    userFeignInfoResponseDto.getUserId(),
                    role,
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

    //attemptAuthentication 인증 실패 로직
    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException failed
    ) {
        try {
            log.warn("인증 실패: {}", failed.getMessage());

            ApiResponse<ExceptionCode> apiResponse = ApiResponse.exception(
                    AuthInfraExceptionCode.UNAUTHORIZED_LOGIN.getMessage(),
                    AuthInfraExceptionCode.UNAUTHORIZED_LOGIN);

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        } catch (IOException e) {
            throw new CustomException(AuthInfraExceptionCode.JSON_WRITE_FAILED, e);
        }
    }
}
