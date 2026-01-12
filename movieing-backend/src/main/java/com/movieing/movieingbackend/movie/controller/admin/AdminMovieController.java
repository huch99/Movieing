package com.movieing.movieingbackend.movie.controller.admin;

import com.movieing.movieingbackend.aspect.ApiResponse;
import com.movieing.movieingbackend.movie.dto.admin.*;
import com.movieing.movieingbackend.movie.service.admin.AdminMovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/movies")
public class AdminMovieController {

    private final AdminMovieService adminMovieService;

    /**
     * 영화 초안 생성 (기본 DRAFT)
     * - body 없이 생성만 하고 싶으면 req를 nullable로 바꾸거나 별도 엔드포인트로 분리해도 됨.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createDraft(@RequestBody(required = false)MovieDraftSaveAdminRequestDto requestDto) {
        Long id = adminMovieService.createDraft(requestDto == null ? MovieDraftSaveAdminRequestDto.builder().build() : requestDto);
        return ResponseEntity.ok(ApiResponse.success(id));
    }

    /**
     * 임시 저장 (DRAFT에서만)
     */
    @PutMapping("/{movieId}/draft")
    public ResponseEntity<ApiResponse<Void>> saveDraft(
            @PathVariable Long movieId,
            @RequestBody @Valid MovieDraftSaveAdminRequestDto requestDto
    ) {
        adminMovieService.saveDraft(movieId, requestDto);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 완료 처리 (DRAFT -> COMING_SOON)
     */
    @PutMapping("/{movieId}/complete")
    public ResponseEntity<ApiResponse<Void>> complete(
            @PathVariable Long movieId,
            @RequestBody @Valid MovieCompleteAdminRequestDto requestDto
    ) {
        adminMovieService.complete(movieId, requestDto);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 상세 수정 (정책에 따라 제한)
     */
    @PutMapping("/{movieId}")
    public ResponseEntity<ApiResponse<Void>> update(
            @PathVariable Long movieId,
            @RequestBody @Valid MovieUpdateAdminRequestDto requestDto
    ) {
        adminMovieService.update(movieId, requestDto);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 숨김 처리
     */
    @PutMapping("/{movieId}/hide")
    public ResponseEntity<ApiResponse<Void>> hide(@PathVariable Long movieId) {
        adminMovieService.hide(movieId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 숨김 해제 -> COMING_SOON 복구 (정책)
     * - 엔티티/서비스에서 전용 메서드로 처리하는 걸 추천
     */
    @PutMapping("/{movieId}/unhide")
    public ResponseEntity<ApiResponse<Void>> unhide(@PathVariable Long movieId) {
        adminMovieService.unhideToComingSoon(movieId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 삭제 (소프트 삭제)
     */
    @DeleteMapping("/{movieId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long movieId) {
        adminMovieService.delete(movieId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 어드민 목록
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<MovieListItemAdminResponseDto>>> getList() {
        return ResponseEntity.ok(ApiResponse.success(adminMovieService.getList()));
    }

    /**
     * 어드민 상세
     */
    @GetMapping("/{movieId}")
    public ResponseEntity<ApiResponse<MovieDetailAdminResponseDto>> getDetail(@PathVariable Long movieId) {
        return ResponseEntity.ok(ApiResponse.success(adminMovieService.getDetail(movieId)));
    }
}
