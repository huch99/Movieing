package com.movieing.movieingbackend.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

public class JwtProvider {

    private final SecretKey key;
    private final long expMillis;

    public JwtProvider(String secret, long expMinutes) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expMillis = expMinutes * 60_000L;
    }

    public String createToken(String subjectUserId, String role) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(subjectUserId)
                .claim("role", role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expMillis)))
                .signWith(key)
                .compact();
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
    }
}
