package com.team15gijo.auth.infrastructure.jwt;

import com.team15gijo.auth.infrastructure.dto.security.AuthLoginResponseCommand;
import com.team15gijo.auth.infrastructure.exception.AuthInfraExceptionCode;
import com.team15gijo.common.exception.CustomException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

    @Value("${jwt.secret.key}")
    private String secretKeyString;

    private Key secretKey;

    @Value("${jwt.access-token.expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration}")
    private Long refreshTokenExpiration;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKeyString);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(AuthLoginResponseCommand authLoginResponseCommand) {
        return Jwts.builder()
                .setSubject(String.valueOf(authLoginResponseCommand.userId()))
                .claim("role", authLoginResponseCommand.roleName())
                .claim("nickname", authLoginResponseCommand.nickname())
                .claim("region", authLoginResponseCommand.region())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(AuthLoginResponseCommand authLoginResponseCommand) {
        return Jwts.builder()
                .setSubject(String.valueOf(authLoginResponseCommand.userId()))
                .claim("role", authLoginResponseCommand.roleName())
                .claim("nickname", authLoginResponseCommand.nickname())
                .claim("region", authLoginResponseCommand.region())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }

    //access토큰 검증 메소드
    public void validateAccessToken(String accessToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(accessToken); //유효성, 서명 검사
        } catch (Exception e) {
            throw new CustomException(AuthInfraExceptionCode.INVALID_ACCESS_TOKEN, e);
        }
    }

    //refresh토큰 검증 메소드
    public void validateRefreshToken(String refreshToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(refreshToken); //유효성, 서명 검사
        } catch (Exception e) {
            throw new CustomException(AuthInfraExceptionCode.INVALID_REFRESH_TOKEN, e);
        }
    }


    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
