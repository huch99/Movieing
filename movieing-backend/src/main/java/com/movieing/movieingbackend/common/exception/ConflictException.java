package com.movieing.movieingbackend.common.exception;

/**
 * 비즈니스 충돌(Conflict)을 의미하는 예외
 * <p>
 * - 요청 자체는 문법적으로 올바르지만,
 * 현재 리소스 상태나 비즈니스 규칙상 처리할 수 없는 경우 사용
 * - GlobalExceptionHandler 에서 HTTP 409(CONFLICT)로 매핑됨
 * <p>
 * 사용 예:
 * - 상태 전이가 허용되지 않는 경우 (예: DRAFT가 아닌 상태에서 complete 요청)
 * - 이미 처리된 리소스에 대한 중복 요청
 * - 현재 상태에서는 수행할 수 없는 작업 요청
 */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
