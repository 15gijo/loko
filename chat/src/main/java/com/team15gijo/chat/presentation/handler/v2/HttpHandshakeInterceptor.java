package com.team15gijo.chat.presentation.handler.v2;

import java.net.URI;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component("httpHandshakeInterceptorV2") // Spring IoC 컨테이너 내 동일한 빈 충돌로 이름 명시
@RequiredArgsConstructor
public class HttpHandshakeInterceptor implements HandshakeInterceptor {

    // 세션 연결 전
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
        WebSocketHandler wsHandler, Map<String, Object> attributes) {
        log.info("[HttpHandshakeInterceptor] beforeHandshake 시작");
        try {
            Long senderId = extractSenderId(request, attributes);
            Long receiverId = extractReceiverId(request, attributes);
            String receiverNickname = extractReceiverNickname(request, attributes);

            log.info("# 쿼리 파라미터에서 추출된 senderId = {}", senderId);
            log.info("# 쿼리 파라미터에서 추출된 receiverId = {}", receiverId);
            log.info("# 쿼리 파라미터에서 추출된 receiverNickname = {}", receiverNickname);

            if (senderId == null) {
                log.error("# 필수 쿼리 파라미터 senderId 추출 실패: 연결 거부");
                return false;
            }

            log.info("[HttpHandshakeInterceptor] beforeHandshake 종료 - 쿼리 파라미터 정보 저장됨!");
            return true;

        } catch (Exception e) {
            log.error("[HttpHandshakeInterceptor] beforeHandshake 예외 발생: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
        WebSocketHandler wsHandler, Exception exception) {

    }

    // WebSocket 연결 요청 시 쿼리파라미터에서 senderId 추출 및 attribute 저장
    private Long extractSenderId(ServerHttpRequest request, Map<String, Object> attributes) {
        try {
            URI uri = request.getURI();
            String senderIdParam = UriComponentsBuilder.fromUri(uri).build()
                .getQueryParams().getFirst("senderId");
            if (StringUtils.hasText(senderIdParam)) {
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

    // WebSocket 연결 요청 시 쿼리파라미터에서 receiverId 추출 및 attribute 저장
    private Long extractReceiverId(ServerHttpRequest request, Map<String, Object> attributes) {
        try {
            URI uri = request.getURI();
            String receiverIdParam = UriComponentsBuilder.fromUri(uri).build()
                .getQueryParams().getFirst("receiverId");
            if (StringUtils.hasText(receiverIdParam)) {
                Long receiverId = Long.parseLong(receiverIdParam);
                attributes.put("receiverId", receiverId);
                log.info("extractReceiverId : receiverId = {}", receiverId);
                return receiverId;
            } else {
                log.warn("receiverId 쿼리 파라미터가 존재하지 않습니다.");
                return null;
            }
        } catch (NumberFormatException e) {
            log.error("receiverId 쿼리 파라미터 파싱 실패: {}", e.getMessage());
            return null;
        }
    }

    // WebSocket 연결 요청 시 쿼리파라미터에서 receiverNickname 추출 및 attribute 저장
    private String extractReceiverNickname(ServerHttpRequest request, Map<String, Object> attributes) {
        URI uri = request.getURI();
        String receiverNicknameParam = UriComponentsBuilder.fromUri(uri).build()
            .getQueryParams().getFirst("receiverNickname");
        if (StringUtils.hasText(receiverNicknameParam)) {
            attributes.put("receiverNickname", receiverNicknameParam);
            log.info("extractReceiverNickname : receiverNickname = {}", receiverNicknameParam);
            return receiverNicknameParam;
        } else {
            log.warn("receiverNickname 쿼리 파라미터가 존재하지 않습니다.");
            return null;
        }
    }
}
