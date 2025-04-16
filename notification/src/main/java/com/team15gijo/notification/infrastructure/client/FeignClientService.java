package com.team15gijo.notification.infrastructure.client;

import com.team15gijo.common.exception.CustomException;
import com.team15gijo.notification.domain.exception.NotificationDomainExceptionCode;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j(topic = "알림 FeignClient")
@Service
@RequiredArgsConstructor
public class FeignClientService {

    private final UserClient userClient;

    @CircuitBreaker(name = "userClient", fallbackMethod = "getUserIdByNicknameFallback")
    public Long getUserIdByNickname(@PathVariable("nickname") String nickname) {
        return userClient.getUserIdByNickname(nickname);
    }

    public Long getUserIdByNicknameFallback(@PathVariable("nickname") String nickname, Throwable t) {
        log.error("User Search Feign Client 호출 실패 (Fallback 처리): {}", t.getMessage());
        throw new CustomException(NotificationDomainExceptionCode.USER_SERVICE_UNAVAILABLE);
    }

}
