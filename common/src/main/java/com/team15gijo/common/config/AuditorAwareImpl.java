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

            String userId = request.getHeader(USER_ID_HEADER);
            if (userId == null || userId.isBlank()) {
                if (request.getRequestURI().contains("/signup")) {
                    log.debug("회원가입");
                    return Optional.empty();
                }
                throw new CustomException(CommonExceptionCode.AUDITOR_HEADER_NOT_FOUND);
            }

            return Optional.of(Long.valueOf(userId));

        } catch (Exception e) {
            throw new CustomException(CommonExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }
}
