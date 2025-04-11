package com.team15gijo.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

@Slf4j
public class LoggingFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    @Override
    public ServerResponse filter(ServerRequest request, HandlerFunction<ServerResponse> next)
            throws Exception {

        String method = request.method().name();
        String path = request.path();
        String requestURI = request.uri().toString();

        log.info("[GATEWAY] : Method: {}, Path: {}, URI: {}", method, path, requestURI);

        ServerResponse response = next.handle(request);

        log.info("[GATEWAY] : Response: {}", response.statusCode());
        return response;
    }
}
