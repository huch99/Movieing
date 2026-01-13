package com.movieing.movieingbackend.config;

import com.movieing.movieingbackend.security.jwt.JwtAuthFilter;
import com.movieing.movieingbackend.security.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 설정 클래스
 * <p>
 * - JWT 기반 인증을 사용하며, 세션을 사용하지 않는 Stateless 구성
 * - JwtAuthFilter를 UsernamePasswordAuthenticationFilter 이전에 배치하여
 * Authorization 헤더의 토큰을 검증하고 SecurityContext에 인증 정보를 세팅
 * - /api/admin/** 는 ADMIN 권한(ROLE_ADMIN)만 접근 가능
 * - /api/** 는 인증 필요
 * - 인증/회원가입 API는 예외적으로 permitAll 처리
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * JwtProvider 빈 등록
     * <p>
     * - application.yml의 app.jwt.* 설정값을 주입받아 JwtProvider 생성
     * - access-token 만료 시간(분 단위)을 함께 사용
     */
    @Bean
    public JwtProvider jwtProvider(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-token-exp-min}") long expMin
    ) {
        return new JwtProvider(secret, expMin);
    }

    /**
     * SecurityFilterChain 구성
     * <p>
     * 주요 설정:
     * - csrf 비활성화 (JWT + Stateless)
     * - cors 활성화 (CorsConfig의 CorsConfigurationSource 사용)
     * - 세션 사용 안 함(STATELESS)
     * - formLogin/httpBasic 비활성화
     * - 경로별 권한 정책 설정
     * - JwtAuthFilter를 UsernamePasswordAuthenticationFilter 이전에 등록
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtProvider jwtProvider) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(fl -> fl.disable())
                .httpBasic(hb -> hb.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // swagger / health
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/actuator/health").permitAll()

                        // auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()

                        // admin
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // rest
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                )

                .addFilterBefore(new JwtAuthFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 비밀번호 암호화용 PasswordEncoder
     * <p>
     * - 사용자 비밀번호 저장 시 BCrypt 해시를 사용
     * - 로그인 시 입력 비밀번호와 해시를 매칭
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
