package com.movieing.movieingbackend.config;

import com.movieing.movieingbackend.user.entity.User;
import com.movieing.movieingbackend.user.entity.UserRole;
import com.movieing.movieingbackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class AdminSeedConfig {

    @Bean
    CommandLineRunner seedAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String adminEmail = "admin@movieing.com";
            if(userRepository.existsByEmail(adminEmail)) return ;

            User admin = User.builder()
                    .publicUserId(UUID.randomUUID().toString())
                    .userName("ADMIN")
                    .email(adminEmail)
                    .passwordHash(passwordEncoder.encode("admin1234!"))
                    .phone("010-0000-0000")
                    .role(UserRole.ADMIN)
                    .isActive(true)
                    .build();

            userRepository.save(admin);
        };
    }
}
