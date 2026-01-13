package com.movieing.movieingbackend.auth.controller;

import com.movieing.movieingbackend.aspect.ApiResponse;
import com.movieing.movieingbackend.auth.dto.*;
import com.movieing.movieingbackend.auth.service.AuthMeService;
import com.movieing.movieingbackend.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 인증(Auth) 관련 API 컨트롤러
 *
 * - 로그인 / 회원가입 / 로그아웃 / 내 정보 조회 기능 제공
 * - 모든 응답은 ApiResponse<T> 형식으로 래핑하여 반환
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthMeService authMeService;

    /**
     * 로그인
     *
     * - 이메일/비밀번호를 검증하여 JWT Access Token 발급
     * - 로그인 성공 시 토큰 및 사용자 기본 정보 반환
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        return ResponseEntity.ok(ApiResponse.success(authService.login(loginRequestDto)));
    }

    /**
     * 회원가입
     *
     * - 신규 사용자 등록
     * - 기본 권한은 USER로 생성
     * - publicUserId 및 약관 동의 시각은 엔티티에서 자동 처리
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponseDto>> signup(
            @Valid @RequestBody SignupRequestDto signupRequestDto
    ) {
        return ResponseEntity.ok(ApiResponse.success(authService.signup(signupRequestDto)));
    }

    /**
     * 로그아웃
     *
     * - JWT 기반 인증 특성상 서버 측 세션 처리 없음
     * - 클라이언트에서 토큰 삭제를 유도하기 위한 더미 API
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 로그인된 사용자 정보 조회
     *
     * - JWT 인증 필터를 통과한 요청만 접근 가능
     * - Authentication 객체의 name(subject)에는 publicUserId가 담김
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MeResponseDto>> me(Authentication authentication) {
        String subject = authentication.getName();
        MeResponseDto dto = authMeService.getMe(subject);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }
}
