package com.movieing.movieingbackend.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

/**
 * JWT 생성 및 검증을 담당하는 클래스
 * <p>
 * 역할:
 * - 사용자 식별자(subject)와 권한(role)을 포함한 JWT 생성
 * - 전달받은 JWT 토큰을 검증하고 Claims 정보를 파싱
 * <p>
 * 특징:
 * - HMAC-SHA 기반 서명 방식 사용
 * - 만료 시간(expiration)을 포함한 Access Token 전용 Provider
 * - 토큰 유효성 검증 실패 시 예외를 발생시켜 상위 레벨에서 처리
 */
public class JwtProvider {

    private final SecretKey key;     // JWT 서명에 사용할 비밀 키
    private final long expMillis;    // 토큰 만료 시간(ms 단위)

    /**
     * JwtProvider 생성자
     *
     * @param secret     JWT 서명용 시크릿 키 (application.yml에서 주입)
     * @param expMinutes Access Token 만료 시간 (분 단위)
     */
    public JwtProvider(String secret, long expMinutes) {
        // 🔒 JWT 시크릿 키 최소 길이 검증 (HS256 기준)
        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException(
                    "JWT secret key는 최소 32자 이상이어야 합니다."
            );
        }

        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expMillis = expMinutes * 60_000L;
    }

    /**
     * JWT Access Token 생성
     *
     * @param subjectUserId 토큰 subject로 사용할 사용자 식별자
     * @param role          사용자 권한 (예: USER, ADMIN)
     * @return 생성된 JWT 문자열
     */
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

    /**
     * JWT 토큰 파싱 및 검증
     * <p>
     * - 서명 검증
     * - 만료 시간(expiration) 검증
     * - 유효하지 않은 경우 예외 발생
     *
     * @param token JWT 문자열
     * @return 파싱된 JWS Claims
     */
    public Jws<Claims> parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
    }
}
