package com.team15gijo.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@Order(-1)
public class LoggingFilter implements GlobalFilter {


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        String method = request.getMethod().toString();
        String path = request.getURI().getPath();
        String requestURI = request.getURI().toString();

        log.info("[GATEWAY] : Method: {}, Path: {}, URI: {}", method, path, requestURI);

        long startTime = System.currentTimeMillis();
        return chain.filter(exchange)
                .doOnSuccess(Void -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("[GATEWAY] : Response: {}, Duration: {}s", response.getStatusCode(),
                            String.format("%.3f", duration / 1000.0));
                });
    }
}
