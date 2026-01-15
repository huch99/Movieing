package com.movieing.movieingbackend.config;

import com.movieing.movieingbackend.user.entity.User;
import com.movieing.movieingbackend.user.entity.UserAdmin;
import com.movieing.movieingbackend.user.entity.UserRole;
import com.movieing.movieingbackend.user.repository.UserAdminRepository;
import com.movieing.movieingbackend.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

/**
 * 관리자 계정 초기 시드 데이터 설정
 * <p>
 * - 애플리케이션 최초 기동 시 관리자 계정이 존재하지 않으면 자동 생성
 * - 개발/테스트 환경에서 관리자 계정을 빠르게 확보하기 위한 용도
 * <p>
 * 동작 방식:
 * - 지정된 관리자 이메일(admin@movieing.com)이 존재하는지 확인
 * - 존재하지 않을 경우 관리자(UserRole.ADMIN) 계정을 생성
 * <p>
 * 주의:
 * - 운영 환경에서는 사용 여부를 반드시 검토하거나 비활성화 필요
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class AdminSeedConfig {

    /**
     * 관리자 계정 시드 데이터 생성
     * <p>
     * - CommandLineRunner를 사용하여 애플리케이션 시작 시 1회 실행
     * - 이미 관리자 계정이 존재하면 아무 작업도 수행하지 않음
     */
    @Bean
    CommandLineRunner seedAdmin(UserRepository userRepository, UserAdminRepository userAdminRepository, PasswordEncoder passwordEncoder , EntityManager em) {
        return args -> {
            log.info("Seeding admin user...");
            String adminEmail = "admin@movieing.com";
            if (userRepository.existsByEmail(adminEmail)) return;

            User admin = User.builder()
                    .userName("ADMIN")
                    .email(adminEmail)
                    .passwordHash(passwordEncoder.encode("admin1234!"))
                    .phone("010-0000-0000")
                    .role(UserRole.ADMIN)
                    .isActive(true)
                    .build();

            User savedAdmin = userRepository.save(admin);

            // ✅ 핵심: managed reference로 연결
            User ref = em.getReference(User.class, savedAdmin.getUserId());

            UserAdmin userAdmin = UserAdmin.builder()
                    .user(ref)
                    .theater(null)
                    .build();

            userAdminRepository.save(userAdmin);

            log.info("Admin user created: {}", savedAdmin.getEmail());
        };
    }
}
