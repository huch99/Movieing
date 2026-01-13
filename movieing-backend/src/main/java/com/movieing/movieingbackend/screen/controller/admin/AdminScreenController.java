package com.movieing.movieingbackend.screen.controller.admin;

import com.movieing.movieingbackend.aspect.ApiResponse;
import com.movieing.movieingbackend.screen.dto.admin.*;
import com.movieing.movieingbackend.screen.service.admin.AdminScreenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/screens")
public class AdminScreenController {

    private final AdminScreenService adminScreenService;

    /**
     * 상영관 목록 조회 (관리자)
     * - DELETED 제외 전체 목록을 조회하는 용도
     * - 어드민 상영관 관리 리스트 화면에서 사용
     */
    @GetMapping
    public ApiResponse<List<ScreenListItemAdminResponseDto>> getList() {
        return ApiResponse.success(adminScreenService.getList());
    }

    /**
     * 상영관 초안(DRAFT) 생성 (관리자)
     * - 빈(혹은 최소값) 상태의 상영관 레코드를 먼저 만들고, 생성된 screenId를 반환
     * - 이후 /{screenId}/draft 저장 → /{screenId}/complete 완료 흐름으로 진행
     *
     * 주의:
     * - 초안 생성이 body 없이 가능하려면 Screen 엔티티의 theater/screenName이 DRAFT 단계에서 null 허용이어야 함
     */
    @PostMapping("/draft")
    public ApiResponse<Long> createDraft() {
        return ApiResponse.success(adminScreenService.createDraft());
    }

    /**
     * 상영관 상세 조회 (관리자)
     * - 특정 screenId의 상세 정보를 조회
     * - 어드민 상영관 상세/수정 화면 진입 시 사용
     */
    @GetMapping("/{screenId}")
    public ApiResponse<ScreenDetailAdminResponseDto> getDetail(@PathVariable Long screenId) {
        return ApiResponse.success(adminScreenService.getDetail(screenId));
    }

    /**
     * 상영관 임시 저장(DRAFT 저장) (관리자)
     * - DRAFT 상태에서만 허용되는 부분 저장(Partial Update) 용도
     * - 입력된 값만 반영하고, null 값은 "변경하지 않음"으로 처리하는 방식이 일반적
     */
    @PutMapping("/{screenId}/draft")
    public ApiResponse<Void> saveDraft(
            @PathVariable Long screenId,
            @RequestBody ScreenDraftSaveAdminRequestDto req
    ) {
        adminScreenService.saveDraft(screenId, req);
        return ApiResponse.success(null);
    }

    /**
     * 상영관 완료 처리 (관리자)
     * - DRAFT 상태의 상영관을 운영 가능한 상태(ACTIVE)로 전환
     * - 완료 시 필수값 검증(@Valid)을 통과해야 함
     */
    @PostMapping("/{screenId}/complete")
    public ApiResponse<Void> complete(
            @PathVariable Long screenId,
            @Valid @RequestBody ScreenCompleteAdminRequestDto req
    ) {
        adminScreenService.complete(screenId, req);
        return ApiResponse.success(null);
    }

    /**
     * 상영관 정보 수정 (관리자)
     *
     * - 운영중(ACTIVE), 숨김(HIDDEN), 운영 종료(CLOSED) 상태의 상영관 정보를 수정
     * - 초안(DRAFT) 상태의 상영관은 수정 불가 (draft 저장 API 사용)
     * - 삭제(DELETED) 상태의 상영관은 수정 불가
     *
     * 수정 방식:
     * - 부분 수정(Partial Update)
     * - 요청 DTO에서 null로 전달된 필드는 "변경하지 않음"으로 처리
     *
     * 검증 책임:
     * - 필수 입력 여부: 없음 (모든 필드 선택 입력)
     * - 상태별 수정 가능 여부: 서비스 레이어
     * - 정책성 검증(수용 인원 범위, 영화관 존재 여부 등): 서비스 레이어
     */
    @PutMapping("/{screenId}")
    public ApiResponse<Void> update(
            @PathVariable Long screenId,
            @RequestBody ScreenUpdateAdminRequestDto req
    ) {
        adminScreenService.update(screenId, req);
        return ApiResponse.success(null);
    }

    /**
     * 상영관 운영중(ACTIVE) 전환 (관리자)
     * - 숨김/종료 상태에서 다시 운영중으로 복구하는 용도
     * - DELETED는 보통 복구 불가(서비스에서 차단 권장)
     */
    @PostMapping("/{screenId}/activate")
    public ApiResponse<Void> activate(@PathVariable Long screenId) {
        adminScreenService.activate(screenId);
        return ApiResponse.success(null);
    }

    /**
     * 상영관 숨김(HIDDEN) 전환 (관리자)
     * - 사용자 노출을 끄고 싶을 때 사용
     * - 데이터는 유지되며, 다시 activate로 복구 가능
     */
    @PostMapping("/{screenId}/hide")
    public ApiResponse<Void> hide(@PathVariable Long screenId) {
        adminScreenService.hide(screenId);
        return ApiResponse.success(null);
    }

    /**
     * 상영관 운영 종료(CLOSED) 전환 (관리자)
     * - 더 이상 운영하지 않는 상영관으로 전환
     * - 상영 스케줄 등록/노출 정책은 서비스 레벨(또는 사용자 조회 API)에서 제어
     */
    @PostMapping("/{screenId}/close")
    public ApiResponse<Void> close(@PathVariable Long screenId) {
        adminScreenService.close(screenId);
        return ApiResponse.success(null);
    }

    /**
     * 상영관 삭제(소프트 삭제, DELETED) (관리자)
     * - 물리 삭제가 아닌 상태값으로 삭제 처리
     * - 목록 조회 시 제외되는 것이 일반적
     */
    @DeleteMapping("/{screenId}")
    public ApiResponse<Void> remove(@PathVariable Long screenId) {
        adminScreenService.remove(screenId);
        return ApiResponse.success(null);
    }
}
