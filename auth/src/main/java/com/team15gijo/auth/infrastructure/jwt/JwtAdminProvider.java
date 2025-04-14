package com.team15gijo.auth.infrastructure.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtAdminProvider {

    @Value("${jwt.admin.secret.key}")
    private String adminSecretKeyString;

    private Key adminSecretKey;

    @PostConstruct
    public void init() {
        log.info("✅ JwtAdminProvider @PostConstruct 시작됨");
        log.info("JWT ADMIN KEY = " + adminSecretKeyString);
        byte[] keyBytes = Decoders.BASE64.decode(adminSecretKeyString);
        log.info("JWT ADMIN KEY bytes = " + new String(keyBytes, StandardCharsets.UTF_8));
        this.adminSecretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public Claims parseToken(String rawToken) {
        return Jwts.parserBuilder()
                .setSigningKey(adminSecretKey)
                .build()
                .parseClaimsJws(rawToken)
                .getBody();
    }
}
