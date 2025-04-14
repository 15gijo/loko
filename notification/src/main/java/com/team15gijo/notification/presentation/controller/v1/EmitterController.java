package com.team15gijo.notification.presentation.controller.v1;


import com.team15gijo.common.exception.CustomException;
import com.team15gijo.notification.application.service.v1.EmitterService;
import com.team15gijo.notification.domain.exception.NotificationDomainExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


/**
 *  클라이언트와의 통신 테스트로 CrossOrigin 허용을 했지만 추후 수정 필요.
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/sse")
@RequiredArgsConstructor
public class EmitterController {

    private final EmitterService emitterService;

    /**
     *  Server-Sent Events(SSE) 연결
     */
    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    public SseEmitter subscribe(@RequestParam("userId") Long userId) {
        if (userId == null) {
            throw new CustomException(NotificationDomainExceptionCode.INVALID_USER);
        }
        System.out.println("🔥 SSE 연결 요청 받음! userId = " + userId);
        return emitterService.subscribe(userId);
    }

}
