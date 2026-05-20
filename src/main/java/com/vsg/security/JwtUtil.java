package com.vsg.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    private final SecretKey key;
    private final long expirationMs;

    public JwtUtil(
        @Value("${jwt.secret}") String secret,
        @Value("${jwt.expiration-ms}") long expirationMs
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String generate(String userId, String email, String tenantId, String[] roles) {
        return Jwts.builder()
            .subject(email)
            .claim("userId", userId)
            .claim("tenantId", tenantId)
            .claim("roles", Arrays.asList(roles))
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expirationMs))
            .signWith(key)
            .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public boolean isValid(String token) {
        try {
            parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getEmail(String token)    { return parse(token).getSubject(); }
    public String getTenantId(String token) { return parse(token).get("tenantId", String.class); }
    public String getUserId(String token)   { return parse(token).get("userId", String.class); }

    @SuppressWarnings("unchecked")
    public List<String> getRoles(String token) {
        return parse(token).get("roles", List.class);
    }
}
