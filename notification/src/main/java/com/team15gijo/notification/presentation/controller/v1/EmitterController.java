package com.team15gijo.notification.presentation.controller.v1;


import com.team15gijo.notification.application.service.v1.EmitterService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/sse")
@RequiredArgsConstructor
public class EmitterController {

    private final EmitterService emitterService;

    /**
     *  Server-Sent Events(SSE) 연결
     */
    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    public SseEmitter subscribe(HttpServletRequest request) {
//        Long userId = extractUserIdFromRequest(request);
        Long userId = 1L;
        return emitterService.subscribe(userId);
    }

//    private Long extractUserIdFromRequest(HttpServletRequest request) {
//        String token = request.getHeader("Authorization");
//        return JwtUtils.extractUserId(token);
//    }

}
