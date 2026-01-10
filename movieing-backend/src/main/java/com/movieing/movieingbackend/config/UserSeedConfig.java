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
public class UserSeedConfig {

    @Bean
    CommandLineRunner seedUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String userEamil = "huichan58@naver.com";
            if(userRepository.existsByEmail(userEamil)) return;

            User user = User.builder()
                    .publicUserId(UUID.randomUUID().toString())
                    .userName("정희찬")
                    .email(userEamil)
                    .passwordHash(passwordEncoder.encode("Siyo9803!"))
                    .phone("010-6247-2008")
                    .role(UserRole.USER)
                    .isActive(true)
                    .build();

            userRepository.save(user);
        };
    }
}
