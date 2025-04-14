package com.team15gijo.gateway.util;

import io.jsonwebtoken.Claims;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.web.server.ServerWebExchange;

public class JwtUtil {

    public static String extractToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    public static String getClaim(Claims claims, String keyName) {
        return claims.get(keyName, String.class);
    }

    public static String getEncodedClaim(Claims claims, String keyName) {
        String rawValue = getClaim(claims, keyName);
        return URLEncoder.encode(rawValue, StandardCharsets.UTF_8);
    }

}
