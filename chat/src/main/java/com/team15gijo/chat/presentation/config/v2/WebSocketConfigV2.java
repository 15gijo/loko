package com.team15gijo.chat.presentation.config.v2;

import com.team15gijo.chat.presentation.handler.v2.HttpHandshakeInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * STOMP를 활용한 WebSOCKET 메시징 기능 활성화
 * */
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfigV2 implements WebSocketMessageBrokerConfigurer {

    private final HttpHandshakeInterceptor httpHandshakeInterceptorV2; // 빈 이름으로 주입

    // STOMP 엔트포인트를 "/ws-stomp" 로 설정하고 SocketJS를 활성화
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/v2/ws-stomp") // ws-stomp 경로로 SocketJS Fallback를 사용해 WebSocket 접속
            .addInterceptors(httpHandshakeInterceptorV2) // HandshakeInterceptor 등록
            .setAllowedOrigins("http://localhost:19097/v2") // 도메인 제한 필요
            .withSockJS();
    }

    /**
     * 메시지 브로커 구성
     * /app 시작으로 들어오는 STOMP 메시지는 @Controller 객체로 라우팅
     * /topic, /queue 시작으로 구독하는 메시지는 메시지 브로커에서 핸들링
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 메시지 구독 주소 prefix: /topic, /queue
        registry.enableSimpleBroker("/topic");
        // 클라이언트에서 메시지 보낼 때 /app으로 시작하면 @MessageMapping 메서드로 라우팅
        registry.setApplicationDestinationPrefixes("/app");
    }
}
