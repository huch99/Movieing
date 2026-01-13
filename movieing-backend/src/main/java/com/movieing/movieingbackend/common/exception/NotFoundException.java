package com.movieing.movieingbackend.common.exception;

/**
 * 리소스를 찾을 수 없을 때 사용하는 예외
 * <p>
 * - 요청한 식별자(ID)에 해당하는 엔티티가 존재하지 않는 경우
 * - 이미 삭제(DELETED)된 리소스에 접근한 경우
 * <p>
 * GlobalExceptionHandler 에서 HTTP 404(NOT_FOUND)로 매핑됨
 * <p>
 * 사용 예:
 * - 존재하지 않는 theaterId / movieId / screenId 조회
 * - 삭제된 리소스에 대한 상세 조회 또는 수정 요청
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
