package com.team15gijo.chat.presentation.handler;

import java.net.URI;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class HttpHandshakeInterceptor implements HandshakeInterceptor {

    // 세션 연결 전
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
        WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        log.info("## HttpHandshakeInterceptor beforeHandshake 시작");
        try {
            Long senderId = extractSenderId(request, attributes);
            log.info("# 쿼리 파라미터에서 추출된 senderId = {}", senderId);
            if (senderId == null) {
                log.error("senderId 쿼리 파라미터 추출 실패: 연결 거부");
                return false;
            } else {
                log.info("## HttpHandshakeInterceptor beforeHandshake 종료: 쿼리 파라미터 senderId = {} 저장됨!", senderId);
                return true;
            }
        } catch (Exception e) {
            log.error("beforeHandshake 예외 발생: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
        WebSocketHandler wsHandler, Exception exception) {

    }

    // WebSocket 연결 요청 시 쿼리파라미터에서 senderId 추출하여 attribute 저장
    private Long extractSenderId(ServerHttpRequest request, Map<String, Object> attributes) {
        try {
            URI uri = request.getURI();
            String senderIdParam = UriComponentsBuilder.fromUri(uri).build().getQueryParams().getFirst("senderId");
            log.info("senderIdParam = {}", senderIdParam);
            if (senderIdParam != null) {
                Long senderId = Long.parseLong(senderIdParam);
                attributes.put("senderId", senderId);
                log.info("extractSenderId : senderId = {}", senderId);
                return senderId;
            } else {
                log.warn("senderId 쿼리 파라미터가 존재하지 않습니다.");
                return null;
            }
        } catch (NumberFormatException e) {
            log.error("senderId 쿼리 파라미터 파싱 실패: {}", e.getMessage());
            return null;
        }
    }
}
