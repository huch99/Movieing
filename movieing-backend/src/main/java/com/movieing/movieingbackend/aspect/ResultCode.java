package com.movieing.movieingbackend.aspect;

/**
 * API 응답 결과 코드
 * <p>
 * - ApiResponse 의 resultCode 필드에서 사용
 * - HTTP Status 와는 별개의 개념으로,
 * 비즈니스 처리 결과의 성공/실패만을 표현
 */
public enum ResultCode {
    SUCCESS,    // 성공
    ERROR       // 실패 (에러)
}
