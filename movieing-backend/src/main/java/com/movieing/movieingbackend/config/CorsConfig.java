package com.movieing.movieingbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * CORS(Cross-Origin Resource Sharing) 설정 클래스
 * <p>
 * - 프론트엔드(React)와 백엔드(Spring Boot) 간의 도메인 차이로 인한
 * 브라우저 CORS 정책을 해결하기 위한 설정
 * - 주로 개발 환경(localhost)에서 API 호출 허용을 목적으로 사용
 * <p>
 * 주요 설정 내용:
 * - 허용 Origin: 로컬 프론트엔드 개발 서버
 * - 허용 Method: GET, POST, PUT, DELETE, OPTIONS
 * - 인증 정보 포함 요청 허용 (쿠키 / Authorization 헤더 등)
 * <p>
 * 참고:
 * - 실제 운영 환경에서는 허용 Origin을 환경별로 분리하는 것이 권장됨
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://127.0.0.1:5173"
        ));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfiguration.setAllowedHeaders(List.of("*"));
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }
}
