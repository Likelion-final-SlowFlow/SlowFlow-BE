package com.slowflow.slowflowbackend.global.config.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final JwtProperties properties;
    private Key key;

    private static final long ACCESS_EXPIRATION = 20 * 60 * 1000L;           // 20분
    private static final long REFRESH_EXPIRATION = 30L * 24 * 60 * 60 * 1000; // 30일


    public JwtTokenProvider(JwtProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(properties.getSecret().getBytes());
    }

    public String createAccessToken(Long memberId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + ACCESS_EXPIRATION);

        return Jwts.builder()
                .setSubject(String.valueOf(memberId))
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(Long memberId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + REFRESH_EXPIRATION);

        return Jwts.builder()
                .setSubject(String.valueOf(memberId))
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Long getMemberId(String token) {
        return Long.parseLong(parseClaims(token).getSubject());
    }

    public boolean isValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}