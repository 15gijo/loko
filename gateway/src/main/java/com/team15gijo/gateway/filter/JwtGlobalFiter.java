package com.team15gijo.gateway.filter;

import com.team15gijo.gateway.security.JwtProvider;
import com.team15gijo.gateway.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtGlobalFiter implements GlobalFilter {

    private final JwtProvider jwtProvider;

    //유효성 제외 주소
    private static final List<String> excludedPaths = List.of(
            "/api/v1/auth/login",
            "/api/v1/users/signup",
            "/api/v1/auth/admin-assign"
    );

    private boolean isExcluded(String path) {
        return excludedPaths.stream().anyMatch(path::startsWith);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        //주소 제외
        if (isExcluded(path)) {
            return chain.filter(exchange);
        }

        //토큰 가져오기
        String token = JwtUtil.extractToken(exchange);
        if (token == null) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        //토큰 헤더 변환
        try {
            Claims claims = jwtProvider.parseToken(token);
            log.info("JWT Token: {}", claims);
            String userId = claims.getSubject();
            String role = JwtUtil.getClaim(claims, "role");
            String nickname = JwtUtil.getEncodedClaim(claims, "nickname");
            String region = JwtUtil.getEncodedClaim(claims, "region");

            ServerHttpRequest mutated = exchange.getRequest().mutate()
                    .header("X-User-Id", userId)
                    .header("X-User-Role", role)
                    .header("X-User-Nickname", nickname)
                    .header("X-User-Region", region)
                    .build();
            log.info("UserId: {}", userId);
            log.info("Role: {}", role);
            log.info("NickName: {}", nickname);
            log.info("NickName: {}", URLDecoder.decode(nickname, StandardCharsets.UTF_8));
            log.info("Region: {}", URLDecoder.decode(nickname, StandardCharsets.UTF_8));
            ServerWebExchange mutatedExchange = exchange.mutate().request(mutated).build();
            return chain.filter(mutatedExchange);
        } catch (JwtException e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

    }
}
