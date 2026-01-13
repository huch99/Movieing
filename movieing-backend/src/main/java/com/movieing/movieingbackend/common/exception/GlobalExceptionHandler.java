package com.movieing.movieingbackend.common.exception;

import com.movieing.movieingbackend.aspect.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /* =========================
       1) Validation
       ========================= */

    /**
     * @Valid 검증 실패 처리 (RequestBody DTO)
     * - 예: @NotBlank, @NotNull 등의 Bean Validation 실패
     * - 첫 번째 필드 에러 메시지를 응답 메시지로 반환
     * - HTTP 400(BAD_REQUEST)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().isEmpty() ? "요청 값이 올바르지 않습니다." : e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(msg));
    }

    /**
     * @Validated 검증 실패 처리 (RequestParam/PathVariable)
     * - 예: @Min, @Max, @Positive 등의 파라미터 검증 실패
     * - HTTP 400(BAD_REQUEST)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("요청 값이 올바르지 않습니다."));
    }

    /* =========================
       2) Custom business exceptions
       ========================= */

    /**
     * 리소스 미존재 예외 처리
     * - HTTP 404(NOT_FOUND)
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
    }

    /**
     * 잘못된 요청 예외 처리 (비즈니스 관점의 400)
     * - HTTP 400(BAD_REQUEST)
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(BadRequestException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
    }

    /**
     * 비즈니스 충돌 예외 처리 (상태 전이 불가 등)
     * - HTTP 409(CONFLICT)
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponse<Void>> handleConflict(ConflictException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(e.getMessage()));
    }

    /* =========================
       3) Standard runtime exceptions
       ========================= */

    /**
     * 잘못된 인자 예외 처리
     * - 주로 엔티티/서비스 레벨의 파라미터 검증 실패에서 발생
     * - HTTP 400(BAD_REQUEST)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
    }

    /**
     * 접근 권한 없음 예외 처리 (Spring Security)
     * - HTTP 403(FORBIDDEN)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("권한이 없습니다."));
    }

    /* =========================
       4) Fallback
       ========================= */

    /**
     * 처리되지 않은 예외의 최종 처리
     * - 내부 구현/스택트레이스 노출 방지
     * - HTTP 500(INTERNAL_SERVER_ERROR)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("서버 오류가 발생했습니다."));
    }
}
