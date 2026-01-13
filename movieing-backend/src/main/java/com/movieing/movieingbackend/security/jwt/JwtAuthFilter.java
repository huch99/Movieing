package com.movieing.movieingbackend.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT 인증 필터
 * <p>
 * - 요청 헤더 Authorization: Bearer {token} 형식을 확인
 * - 토큰이 있으면 JwtProvider로 파싱/검증 후, 인증 정보를 SecurityContext에 저장
 * - 토큰이 없으면 인증 없이 다음 필터로 진행
 * <p>
 * 주의:
 * - 토큰 파싱 실패 시 SecurityContext를 비우고(무인증) 다음 필터로 진행
 * - 실제 인가(권한 체크)는 SecurityConfig의 authorizeHttpRequests / @PreAuthorize에서 수행
 */
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    public JwtAuthFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    /**
     * 요청 당 한 번 실행되는 JWT 인증 처리 로직
     * <p>
     * 처리 흐름:
     * 1) Authorization 헤더 확인
     * 2) Bearer 토큰이면 파싱
     * 3) subject(userId)와 role 클레임을 기반으로 Authentication 생성
     * 4) SecurityContextHolder에 세팅
     * 5) 실패 시 인증 정보 제거 후 통과
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                Jws<Claims> claims = jwtProvider.parse(token);
                String userId = claims.getPayload().getSubject();
                if (userId == null || userId.isBlank()) throw new IllegalArgumentException("subject missing");

                String role = claims.getPayload().get("role", String.class);
                if (role == null || role.isBlank()) throw new IllegalArgumentException("role claim missing");

                var auth = new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role))
                );

                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
