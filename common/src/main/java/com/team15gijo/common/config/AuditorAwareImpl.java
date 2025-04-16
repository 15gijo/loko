package com.team15gijo.common.config;

import com.team15gijo.common.exception.CommonExceptionCode;
import com.team15gijo.common.exception.CustomException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Component
public class AuditorAwareImpl implements AuditorAware<Long> {

    private static final String USER_ID_HEADER = "X-User-Id";

    @Override
    public Optional<Long> getCurrentAuditor() {
        try {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

            if (!(requestAttributes instanceof ServletRequestAttributes)) {
//                throw new CustomException(CommonExceptionCode.AUDITOR_NON_MVC_REQUEST);
                log.warn("🟡 AuditorAware: 비동기 컨텍스트 감지, Optional.empty 반환");
                return Optional.empty(); // ⭐ Kafka나 Scheduler에서 호출될 경우
            }

            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
            HttpServletRequest request = servletRequestAttributes.getRequest();

            String requestURI = request.getRequestURI();
            log.info("📌 Auditor 호출됨: URI={}", requestURI);
            if (requestURI.contains("/admin-assign")) {
                log.info("✅ trusted-admin API 호출 → 감사자: trusted-admin");
                return Optional.of(Long.valueOf("0000"));
            }

            String userId = request.getHeader(USER_ID_HEADER);
            log.info("📌 Auditor 호출됨: userId={}", userId);
            if (userId == null || userId.isBlank()) {
                if (request.getRequestURI().contains("/signup")) {
                    return Optional.empty();
                }
                throw new CustomException(CommonExceptionCode.AUDITOR_HEADER_NOT_FOUND);
//                return Optional.empty();  // 댓글 생성시 내부 client 통신할 때 auditor에 걸려서 임시로 리턴값 넣었습니다.
            }

            return Optional.of(Long.valueOf(userId));

        } catch (Exception e) {
            throw new CustomException(CommonExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }
}
