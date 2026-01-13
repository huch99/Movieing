package com.movieing.movieingbackend.auth.service;

import com.movieing.movieingbackend.auth.dto.MeResponseDto;
import com.movieing.movieingbackend.common.exception.ConflictException;
import com.movieing.movieingbackend.common.exception.NotFoundException;
import com.movieing.movieingbackend.user.entity.User;
import com.movieing.movieingbackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 인증된 사용자의 "내 정보 조회"를 담당하는 서비스
 * <p>
 * - JWT 인증 필터를 통과한 요청만 접근 가능
 * - JWT의 subject(sub)에 담긴 publicUserId를 기준으로 사용자 조회
 * - 조회된 사용자 정보를 MeResponseDto로 변환하여 반환
 */
@Service
@RequiredArgsConstructor
public class AuthMeService {

    private final UserRepository userRepository;

    /**
     * 내 정보 조회
     * <p>
     * 처리 흐름:
     * 1) JWT subject(sub)로 전달된 publicUserId 기반 사용자 조회
     * 2) 사용자가 존재하지 않으면 예외 발생
     * 3) 사용자 엔티티를 MeResponseDto로 변환하여 반환
     *
     * @param subject JWT 토큰의 subject 값 (publicUserId)
     * @return MeResponseDto 로그인된 사용자 정보
     * <p>
     * 트랜잭션 정책:
     * - 조회 전용 로직이므로 readOnly = true 설정
     */
    @Transactional(readOnly = true)
    public MeResponseDto getMe(String subject) {

        // 1) subject가 UUID 문자열인지 형식 검증(선택이지만 추천)
        try {
            java.util.UUID.fromString(subject);
        } catch (IllegalArgumentException e) {
            throw new ConflictException("잘못된 사용자 식별자입니다.");
        }

        // 2) 사용자 조회
        User user = userRepository.findByPublicUserId(subject)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        // 3) 활성 상태 체크(선택이지만 추천)
        if (!user.isActive()) {
            throw new ConflictException("비활성화된 계정입니다.");
        }

        // 4) 응답 DTO 변환
        return MeResponseDto.builder()
                .publicUserId(user.getPublicUserId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}
