package com.movieing.movieingbackend.auth.service;

import com.movieing.movieingbackend.auth.dto.LoginRequestDto;
import com.movieing.movieingbackend.auth.dto.LoginResponseDto;
import com.movieing.movieingbackend.auth.dto.SignupRequestDto;
import com.movieing.movieingbackend.auth.dto.SignupResponseDto;
import com.movieing.movieingbackend.common.exception.ConflictException;
import com.movieing.movieingbackend.security.jwt.JwtProvider;
import com.movieing.movieingbackend.user.entity.User;
import com.movieing.movieingbackend.user.entity.UserRole;
import com.movieing.movieingbackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    /**
     * 로그인 처리
     *
     * 처리 흐름:
     * 1) 이메일로 사용자 조회
     * 2) 활성(isActive) 상태 확인
     * 3) 비밀번호 해시 검증
     * 4) JWT Access Token 발급
     * 5) 로그인 응답 DTO 반환
     *
     * 예외 정책:
     * - 존재하지 않는 사용자 / 비밀번호 불일치 / 비활성 계정은 ConflictException으로 통일(409)
     */
    @Transactional
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {

        // 1) 사용자 조회
        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new ConflictException("존재하지 않는 사용자입니다."));

        // 2) 활성 상태 체크
        if (!user.isActive()) {
            throw new ConflictException("비활성화된 계정입니다.");
        }

        // 3) 비밀번호 검증
        if(!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPasswordHash())) {
            throw new ConflictException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        // 4) JWT 발급
        String accessToken = jwtProvider.createToken(
                user.getPublicUserId(),
                user.getRole().name()
        );

        // 5) 응답
        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .publicUserId(user.getPublicUserId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    /**
     * 회원가입 처리
     *
     * 처리 흐름:
     * 1) 이메일 중복 체크
     * 2) 비밀번호 해시 생성
     * 3) User 엔티티 생성 및 저장
     * 4) 회원가입 응답 DTO 반환
     *
     * 설계 원칙:
     * - publicUserId/agreeAt 등 엔티티 기본값은 User 엔티티(@PrePersist)가 책임지는 것을 권장
     * - 회원가입은 기본 USER 권한으로 생성
     */
    @Transactional
    public SignupResponseDto signup(SignupRequestDto signupRequestDto) {

        // 1) 이메일 중복 체크
        if (userRepository.existsByEmail(signupRequestDto.getEmail())) {
            throw new ConflictException("이미 사용 중인 이메일입니다.");
        }

        // 2) 비밀번호 해시
        String passwordHash = passwordEncoder.encode(signupRequestDto.getPassword());

        // 3) 엔티티 생성
        User user = User.builder()
                .userName(signupRequestDto.getUserName())
                .email(signupRequestDto.getEmail())
                .passwordHash(passwordHash)
                .phone(signupRequestDto.getPhone())
                .role(UserRole.USER)
                .isActive(true)
                .build();

        User saved = userRepository.save(user);

        // 4) 응답
        return SignupResponseDto.builder()
                .publicUserId(saved.getPublicUserId())
                .userName(saved.getUserName())
                .email(saved.getEmail())
                .role(saved.getRole())
                .build();

    }
}
