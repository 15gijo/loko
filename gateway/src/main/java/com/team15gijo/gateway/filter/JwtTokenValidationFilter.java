package com.team15gijo.gateway.filter;

import com.team15gijo.common.exception.CommonExceptionCode;
import com.team15gijo.common.exception.CustomException;
import com.team15gijo.gateway.util.HeaderRequestWrapper;
import com.team15gijo.gateway.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtTokenValidationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    //유효성 제외 주소
    private static final List<String> excludedPaths = List.of(
            "/auth",
            "/user"
    );


    private boolean isExcluded(String path) {
        return excludedPaths.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        //유효성 검사 제외
        if (isExcluded(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        //토큰 가져오기
        String token = jwtUtil.extractToken(request);
        if (token == null) {
            throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS);
        }

        //토큰 유효성 검사
        try {
            Claims claims = jwtUtil.parseToken(token);
            String userId = claims.getSubject();

            HttpServletRequest wrappedRequest = new HeaderRequestWrapper(request)
                    .addHeader("X-User-Id", userId)
                    .addHeader("X-User-Role", token);
            //헤더 리턴
            filterChain.doFilter(wrappedRequest,response);

        } catch (JwtException e) {
            throw new CustomException(CommonExceptionCode.UNAUTHORIZED_ACCESS, e);
        }

    }

}