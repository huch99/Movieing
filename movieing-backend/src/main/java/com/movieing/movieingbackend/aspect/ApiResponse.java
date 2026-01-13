package com.movieing.movieingbackend.aspect;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 공통 API 응답 래퍼
 * <p>
 * - 모든 컨트롤러의 응답은 ApiResponse 형태로 반환
 * - HTTP Status 코드는 GlobalExceptionHandler 에서 관리
 * - 컨트롤러는 정상 처리 흐름만 담당
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

    private ResultCode resultCode;      // 결과 코드 (SUCCESS / ERROR)
    private String resultMessage;       // 결과 메시지 (성공: "SUCCESS", 실패: 에러 메시지)
    private T data;                     // 실제 응답 데이터 (실패 시 null)


    /**
     * 성공 응답 생성
     *
     * @param data 응답 데이터 (없을 경우 null 허용)
     * @param <T>  데이터 타입
     * @return 성공 ApiResponse
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .resultCode(ResultCode.SUCCESS)
                .resultMessage("SUCCESS")
                .data(data)
                .build();
    }

    /**
     * 실패 응답 생성
     * <p>
     * - HTTP Status 는 GlobalExceptionHandler 에서 결정
     * - 이 메서드는 응답 바디 형태만 통일하는 역할
     *
     * @param resultMessage 에러 메시지
     * @param <T>           데이터 타입
     * @return 실패 ApiResponse
     */
    public static <T> ApiResponse<T> error(String resultMessage) {
        return ApiResponse.<T>builder()
                .resultCode(ResultCode.ERROR)
                .resultMessage(resultMessage)
                .data(null)
                .build();
    }
}
