package com.team15gijo.notification.presentation.controller.v1;

import com.team15gijo.notification.application.dto.v1.NotificationResponseDto;
import com.team15gijo.notification.application.service.v1.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *  클라이언트와의 통신 테스트로 CrossOrigin 허용을 했지만 추후 수정 필요.
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    /**
     *  미확인 알림 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<NotificationResponseDto>> getUnreadNotifications(
            HttpServletRequest request) {
//        Long userId = extractUserIdFromRequest(request);
        Long userId = 1L;
        return ResponseEntity.ok(notificationService.getUnreadNotifications(userId));
    }



    /**
     *   알림 읽음 처리
     */
    @PatchMapping ("/{notificationId}/read")
    public void markAsRead(@PathVariable UUID notificationId, HttpServletRequest request) {
//        Long userId = extractUserIdFromRequest(request);
        Long userId = 1L;
        notificationService.updateIsChecked(notificationId, userId);
    }

//    private Long extractUserIdFromRequest(HttpServletRequest request) {
//        String token = request.getHeader("Authorization");
//        return JwtUtils.extractUserId(token); // 구현 필요
//    }


    /**
     *   읽지 않은 알림 수
     */
}
