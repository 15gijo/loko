package com.team15gijo.gateway.filter;

import com.team15gijo.gateway.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

@RequiredArgsConstructor
public class JwtTokenValidationFilter implements
        HandlerFilterFunction<ServerResponse, ServerResponse> {

    private final JwtUtil jwtUtil;

    //유효성 제외 주소
    private static final List<String> excludedPaths = List.of(
            "/api/v1/auth/login",
            "/api/v1/users/signup",
            "/api/v1/internal/**"
    );


    private boolean isExcluded(String path) {
        return excludedPaths.stream().anyMatch(path::startsWith);
    }

    @Override
    public ServerResponse filter(
            ServerRequest request,
            HandlerFunction<ServerResponse> next)
            throws Exception {

        String path = request.path();

        //필터 제외
        if (isExcluded(path)) {
            return next.handle(request);
        }

        //토큰 가져오기
        String token = jwtUtil.extractToken(request.servletRequest());
        if (token == null) {
            return ServerResponse.status(401).body("토큰이 없습니다.");
        }

        //토큰 유효성 검사
        try {
            Claims claims = jwtUtil.parseToken(token);
            String userId = claims.getSubject();
            String role = claims.get("role", String.class);
            String nickname = claims.get("nickname", String.class);
            String region = claims.get("region", String.class);

            /**
             * 한글이 깨져서 인코딩 해주었습니다. post 에서는 디코딩해서 db에 저장하였습니다.
             */
            String encodedRegion = URLEncoder.encode(region, StandardCharsets.UTF_8.toString());


            //헤더 추가
            ServerRequest mutated = ServerRequest.from(request)
                    .header("X-User-Id", userId)
                    .header("X-User-Role", role)
                    .header("X-User-Nickname", nickname)
                    .header("X-User-Region", encodedRegion)
                    .build();

            return next.handle(mutated);
        } catch (JwtException e) {
//            throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS, e);
            throw new RuntimeException("Token is invalid", e);
        }
    }
}