package com.movieing.movieingbackend.common.exception;

/**
 * 잘못된 요청을 의미하는 예외
 * <p>
 * - 클라이언트 요청 값 자체가 유효하지 않은 경우 사용
 * - GlobalExceptionHandler 에서 HTTP 400(BAD_REQUEST)로 매핑됨
 * <p>
 * 사용 예:
 * - 필수 파라미터 누락
 * - 허용되지 않은 값 전달
 * - 형식은 맞지만 비즈니스적으로 잘못된 요청
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
