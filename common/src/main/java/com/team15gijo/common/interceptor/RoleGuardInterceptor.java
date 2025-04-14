package com.team15gijo.common.interceptor;

import com.team15gijo.common.annotation.RoleGuard;
import com.team15gijo.common.support.RoleGuardSupport;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoleGuardInterceptor implements HandlerInterceptor {

    private final RoleGuardSupport roleGuardSupport;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws Exception {

        //컨트롤러 mvc 매서드 아닌 경우 통과
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        //어노테이션 없는 경우 통과
        RoleGuard roleGuard = handlerMethod.getMethodAnnotation(RoleGuard.class);
        if (roleGuard == null) {
            return true;
        }

        //헤더에서 유저 롤 추출
        String userRole = request.getHeader("X-User-Role");
        if (userRole == null || userRole.isBlank()) {
            log.warn("[Role Guard] - 유저 롤 없음");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("접근 권한이 없습니다.");
            return false;
        }

        //롤 검사
        boolean match = false;
        String[] roles = roleGuard.value();
        String minRole = roleGuard.min();
        log.info("roles: {}", roles);
        log.info("minRole: {}", minRole);

        if (roles.length > 0) {
            match = roleGuardSupport.isEqualRoleName(userRole, roleGuard.value());
            log.info("match-value: {}", match);
        } else if (!minRole.isBlank()) {
            match = roleGuardSupport.checkUserRoleLevel(userRole, roleGuard.min());
            log.info("match-min: {}", match);
        } else {
            log.warn("[Role Guard] - 어노테이션 value, minRole 모두 없음 (허용 불가)");
        }
        if (!match) {
            log.warn("[Role Guard] - 권한 불일치 ");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("접근 권한이 일치 하지 않습니다.");
            return false;
        }
        return true;
    }

}
