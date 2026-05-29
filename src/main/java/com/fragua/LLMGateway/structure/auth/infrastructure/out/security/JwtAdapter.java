package com.fragua.LLMGateway.structure.auth.infrastructure.out.security;

import com.fragua.LLMGateway.structure.user.domain.model.UserModel;
import com.fragua.LLMGateway.structure.auth.application.port.output.JwtPort;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtAdapter implements JwtPort {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access-expiration}")
    private Long accessExpiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor( jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String generateAccessToken(UserModel user) {

        Date now = new Date();
        Date expiration = new Date( now.getTime() + accessExpiration);

        return Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(now)
                .expiration(expiration)
                .claim("userId", user.getId())
                .claim("username", user.getUsername())
                .signWith(key)
                .compact();
    }

    @Override
    public String generateRefreshToken(UserModel user) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + refreshExpiration);
        return Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

    @Override
    public Long getAccessTokenExpiration() {

        return accessExpiration;
    }

    @Override
    public String extractUsername(String token) {

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    @Override
    public boolean isTokenValid(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}