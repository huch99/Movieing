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

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public JwtProvider jwtProvider(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-token-exp-min}") long expMin
    ) {
        return new JwtProvider(secret, expMin);
    }

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
                        .requestMatchers("/swagger-ul/**", "/v3/api-docs/**", "/actuator/health").permitAll()

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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
