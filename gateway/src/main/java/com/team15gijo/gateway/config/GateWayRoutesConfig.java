package com.team15gijo.gateway.config;

import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

import com.team15gijo.gateway.filter.JwtTokenValidationFilter;
import com.team15gijo.gateway.filter.LoggingFilter;
import com.team15gijo.gateway.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
@RequiredArgsConstructor
public class GateWayRoutesConfig {

    private final JwtUtil jwtUtil;

    @Bean
    public RouterFunction<ServerResponse> gatewayRoutes() {
        JwtTokenValidationFilter jwtFilter = new JwtTokenValidationFilter(jwtUtil);
        LoggingFilter loggingFilter = new LoggingFilter();

        return route("user-service")
                .route(path("/api/v1/users/**"), http())
                .filter(lb("user-service"))
                .filter(loggingFilter)
                .filter(jwtFilter)
                .build()
                .and(route("auth-service")
                        .route(path("/api/v1/auth/**"), http())
                        .filter(lb("auth-service"))
                        .filter(loggingFilter)
                        .filter(jwtFilter)
                        .build())
                .and(route("post-service")
                        .route(path("/api/v1/posts/**"), http())
                        .filter(lb("post-service"))
                        .filter(loggingFilter)
                        .filter(jwtFilter)
                        .build())
                .and(route("comment-service")
                        .route(path("/api/v1/comments/**"), http())
                        .filter(lb("comment-service"))
                        .filter(loggingFilter)
                        .filter(jwtFilter)
                        .build());
    }
}
