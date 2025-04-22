//package com.team15gijo.gateway.util;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jws;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;
//import jakarta.annotation.PostConstruct;
//
//import java.security.Key;
//import javax.crypto.SecretKey;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//
//@Component
//public class deprecatedJwtUtil {
//
//    @Value("${jwt.secret.key}")
//    private String secretKeyString;
//
//    private Key secretKey;
//
//    @PostConstruct
//    public void init() {
//        /**
//         * post 생성시에 에러가 발생해서 추가하였습니다!
//         */
//        byte[] keyBytes = Decoders.BASE64.decode(secretKeyString);
//        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
//    }
//
////    public String extractToken(HttpServletRequest request) {
////        String header = request.getHeader("Authorization");
////        if (header != null && header.startsWith("Bearer ")) {
////            return header.substring(7).trim();
////        }
////        return null;
////    }
//    public String extractToken(ServerWebExchange exchange) {
//        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            return authHeader.substring(7);
//        }
//        return null;
//    }
//
//    //파싱 및 유효성 검사
//    public Claims parseToken(String token) {
//        try {
//            SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKeyString));
//            Jws<Claims> claimsJws = Jwts.parserBuilder()
//                    .setSigningKey(key)
//                    .build()
//                    .parseClaimsJws(token);
//
//            return claimsJws.getBody();
//        } catch (Exception e) {
//            throw new RuntimeException("token jwt error", e);
//        }
//    }
////    public Claims parseToken(String token) {
////        try {
////            return Jwts.parserBuilder()
////                    .setSigningKey(secretKey)
////                    .build()
////                    .parseClaimsJws(token)
////                    .getBody();
////        } catch (ExpiredJwtException e) {
//////            throw new CustomException(CommonExceptionCode.EXPIRED_TOKEN, e);
////            throw new RuntimeException("token expired", e);
////        } catch (JwtException e) {
//////            throw new CustomException(CommonExceptionCode.INVALID_TOKEN, e);
////            throw new RuntimeException("token jwt error", e);
////        }
////    }
//}