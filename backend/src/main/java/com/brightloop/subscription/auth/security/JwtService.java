package com.brightloop.subscription.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey key;
    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-token-expiration-ms:900000}") long accessTokenExpirationMs,
            @Value("${app.jwt.refresh-token-expiration-ms:604800000}") long refreshTokenExpirationMs
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }

    public String generateAccessToken(Long userId, String email, String fullName, String role, Long customerId) {
        return buildToken(userId, email, fullName, role, customerId, "ACCESS", accessTokenExpirationMs);
    }

    public String generateRefreshToken(Long userId, String email, String fullName, String role, Long customerId) {
        return buildToken(userId, email, fullName, role, customerId, "REFRESH", refreshTokenExpirationMs);
    }

    private String buildToken(Long userId, String email, String fullName, String role, Long customerId, String type, long expirationMs) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        var builder = Jwts.builder()
                .subject(userId.toString())
                .claim("email", email)
                .claim("fullName", fullName)
                .claim("role", role)
                .claim("type", type)
                .issuedAt(now)
                .expiration(expiry);

        if (customerId != null) {
            builder.claim("customerId", customerId);
        }

        return builder.signWith(key).compact();
    }

    public Claims parseAndValidateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    public UserPrincipal extractPrincipal(Claims claims) {
        if (claims == null) {
            return null;
        }
        Long userId = Long.valueOf(claims.getSubject());
        String email = claims.get("email", String.class);
        String fullName = claims.get("fullName", String.class);
        String role = claims.get("role", String.class);
        Number custIdNum = claims.get("customerId", Number.class);
        Long customerId = custIdNum != null ? custIdNum.longValue() : null;

        return new UserPrincipal(userId, email, fullName, role, customerId);
    }
}

