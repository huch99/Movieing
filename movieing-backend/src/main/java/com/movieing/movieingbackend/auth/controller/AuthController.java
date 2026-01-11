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

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthMeService authMeService;

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        return ResponseEntity.ok(ApiResponse.success(authService.login(loginRequestDto)));
    }

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponseDto>> signup(
            @Valid @RequestBody SignupRequestDto signupRequestDto
    ) {
        return ResponseEntity.ok(ApiResponse.success(authService.signup(signupRequestDto)));
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // 로그인 유저 정보 가져오기
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MeResponseDto>> me(Authentication authentication) {
        String subject = authentication.getName();
        MeResponseDto dto = authMeService.getMe(subject);
        return ResponseEntity.ok(ApiResponse.success((authMeService.getMe(subject))));
    }
}
