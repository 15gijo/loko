package com.team15gijo.gateway.filter;

import com.team15gijo.gateway.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import java.util.List;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

//@RequiredArgsConstructor
public class JwtTokenValidationFilterV2 implements GlobalFilter {

    private final JwtUtil jwtUtil = new JwtUtil();

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
            String userId = claims.getSubject();
            String role = claims.get("role", String.class);
            String nickname = claims.get("nickname", String.class);
            String region = claims.get("region", String.class);

            /**
             * 한글이 깨져서 인코딩 해주었습니다. post 에서는 디코딩해서 db에 저장하였습니다.
             */
//            String encodedRegion = URLEncoder.encode(region, StandardCharsets.UTF_8.toString());



            //헤더 추가
//            ServerRequest mutated = ServerRequest.from(request)
//                    .header("X-User-Id", userId)
//                    .header("X-User-Role", role)
//                    .header("X-User-Nickname", nickname)
//                    .header("X-User-Region", region)
//                    .param("keyword", URLEncoder.encode("한국", StandardCharsets.UTF_8.toString()))
//                    .build();
            exchange.getRequest().mutate()
                    .header("X-User-Id", userId)
                    .header("X-User-Nickname", nickname)
                    .header("X-User-Region", region)
                    .build();

            return chain.filter(exchange);
        } catch (JwtException e) {
//            throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS, e);
            throw new RuntimeException("Token is invalid", e);
        }
    }
}