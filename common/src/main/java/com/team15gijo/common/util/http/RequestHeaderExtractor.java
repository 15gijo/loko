package com.team15gijo.common.util.http;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RequestHeaderExtractor {

    public static Long extractUserId(HttpServletRequest request) {
        try {
            String userId = request.getHeader("X-User-Id");
            return (userId != null) ? Long.parseLong(userId) : null;
        } catch (NumberFormatException e) {
            log.warn(e.getMessage());
            return null;
        }
    }

    public static String extractRole(HttpServletRequest request) {
        return request.getHeader("X-User-Role");
    }

    public static String extractNickname(HttpServletRequest request) {
        return request.getHeader("X-User-Nickname");
    }

    public static String extractRegion(HttpServletRequest request) {
        return request.getHeader("X-User-Region");
    }
}
