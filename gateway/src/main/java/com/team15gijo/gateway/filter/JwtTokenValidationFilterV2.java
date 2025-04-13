package com.team15gijo.gateway.filter;

import com.team15gijo.gateway.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

//@RequiredArgsConstructor
@Component
public class JwtTokenValidationFilterV2 implements GlobalFilter {

    private final JwtUtil jwtUtil;

    public JwtTokenValidationFilterV2(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

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
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        //필터 제외
        if (isExcluded(path)) {
            return chain.filter(exchange);
        }

        //토큰 가져오기
        String token = jwtUtil.extractToken(exchange);
        if (token == null) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        //토큰 유효성 검사
        try {
            Claims claims = jwtUtil.parseToken(token);
            System.out.println("✅ JWT Claims: " + claims);

            String userId = claims.getSubject();
            String role = claims.get("role", String.class);
            String nickname = claims.get("nickname", String.class);
            String region = claims.get("region", String.class);

            System.out.println("➡ region: " + region);

            String encodedRegion = URLEncoder.encode(region, StandardCharsets.UTF_8);

            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("X-User-Id", userId)
                    .header("X-User-Nickname", nickname)
                    .header("X-User-Region", encodedRegion)
                    .build();

            ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();

            return chain.filter(mutatedExchange);
        } catch (JwtException e) {
//            throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS, e);
            throw new RuntimeException("Token is invalid", e);
        }
    }
}