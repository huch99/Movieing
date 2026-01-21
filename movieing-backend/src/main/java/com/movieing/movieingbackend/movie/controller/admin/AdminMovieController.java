package com.movieing.movieingbackend.movie.controller.admin;

import com.movieing.movieingbackend.aspect.ApiResponse;
import com.movieing.movieingbackend.movie.dto.admin.*;
import com.movieing.movieingbackend.movie.service.admin.AdminMovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/movies")
public class AdminMovieController {

    private final AdminMovieService adminMovieService;

    /**
     * 영화 초안 생성 (기본 DRAFT)
     *
     * - body 없이도 "빈 초안"을 만들 수 있음
     * - Draft는 모든 필드 optional이므로 @Valid 검증은 걸지 않음
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createDraft(@RequestBody(required = false) MovieDraftSaveAdminRequestDto requestDto) {
        MovieDraftSaveAdminRequestDto safeDto =
                (requestDto != null) ? requestDto : MovieDraftSaveAdminRequestDto.builder().build();

        Long id = adminMovieService.createDraft(safeDto);
        return ResponseEntity.ok(ApiResponse.success(id));
    }

    /**
     * 임시 저장 (DRAFT에서만)
     *
     * - 부분 저장이므로 전달된 값만 반영
     * - 들어오는 값에 대한 최소 형식/범위 검증은 DTO Validation으로 처리 가능
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
     *
     * - 완료 단계 필수값은 DTO Validation(@Valid)로 검증
     * - 상태 전이 규칙은 서비스/엔티티에서 처리
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
     * 상세 수정 (부분 수정)
     *
     * - 정책상 DELETED는 수정 불가(서비스에서 처리)
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
     * 숨김 처리 (사용자 화면 노출 X)
     */
    @PutMapping("/{movieId}/hide")
    public ResponseEntity<ApiResponse<Void>> hide(@PathVariable Long movieId) {
        adminMovieService.hide(movieId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 숨김 해제(복구)
     *
     * - 서비스 정책에 따라
     *   releaseDate <= today 이면 NOW_SHOWING,
     *   아니면 COMING_SOON으로 복구
     */
    @PutMapping("/{movieId}/unhide")
    public ResponseEntity<ApiResponse<Void>> unhide(@PathVariable Long movieId) {
        adminMovieService.unhide(movieId);
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
     * 어드민 목록 (Page)
     *
     * 기본 정렬: createdAt DESC
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<MovieListItemAdminResponseDto>>> getList(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(adminMovieService.getList(pageable)));
    }

    /**
     * 어드민 상세
     */
    @GetMapping("/{movieId}")
    public ResponseEntity<ApiResponse<MovieDetailAdminResponseDto>> getDetail(@PathVariable Long movieId) {
        return ResponseEntity.ok(ApiResponse.success(adminMovieService.getDetail(movieId)));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<MovieStatsAdminResponseDto>> getStats() {
        MovieStatsAdminResponseDto stats = adminMovieService.getStats();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}
