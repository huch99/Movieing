package com.movieing.movieingbackend.screen.service.admin;

import com.movieing.movieingbackend.common.exception.ConflictException;
import com.movieing.movieingbackend.common.exception.NotFoundException;
import com.movieing.movieingbackend.screen.dto.admin.*;
import com.movieing.movieingbackend.screen.entity.Screen;
import com.movieing.movieingbackend.screen.entity.ScreenStatus;
import com.movieing.movieingbackend.screen.repository.ScreenRepository;
import com.movieing.movieingbackend.theater.entity.Theater;
import com.movieing.movieingbackend.theater.entity.TheaterStatus;
import com.movieing.movieingbackend.theater.repository.TheaterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminScreenService {

    private final ScreenRepository screenRepository;
    private final TheaterRepository theaterRepository;

    /**
     * 상영관 목록 조회 (관리자)
     * - DELETED 제외 목록 조회
     */
    public List<ScreenListItemAdminResponseDto> getList() {
        return screenRepository.findAll().stream()
                .filter(screen -> screen.getStatus() != ScreenStatus.DELETED)
                .map(this::toListItemDto)
                .toList();
    }

    /**
     * 상영관 상세 조회 (관리자)
     */
    public ScreenDetailAdminResponseDto getDetail(Long screenId) {
        Screen screen = getEntity(screenId);
        return toDetailDto(screen);
    }

    /**
     * 상영관 초안(DRAFT) 생성 (관리자)
     * - 빈(혹은 최소값) 상태의 상영관 레코드를 먼저 만들고, 생성된 screenId를 반환
     * - 이후 /{screenId}/draft 저장 → /{screenId}/complete 완료 흐름으로 진행
     *
     * 전제:
     * - Screen 엔티티의 theater/screenName 컬럼이 DRAFT 단계에서 null 허용 상태여야 함
     */
    @Transactional
    public Long createDraft() {
        Screen screen = Screen.builder()
                .status(ScreenStatus.DRAFT)
                .capacity(0)
                .build();

        return screenRepository.save(screen).getScreenId();
    }

    /**
     * 상영관 임시 저장(DRAFT 저장) (관리자)
     *
     * - DRAFT 상태에서만 허용되는 부분 저장(Partial Update)
     * - 입력된 값만 반영하고, null 값은 "변경하지 않음"으로 처리
     */
    @Transactional
    public void saveDraft(Long screenId, ScreenDraftSaveAdminRequestDto req) {
        Screen screen = getEntity(screenId);

        if (screen.getStatus() != ScreenStatus.DRAFT) {
            throw new ConflictException("초안 상태(DRAFT)에서만 임시 저장이 가능합니다.");
        }

        // 소속 영화관 변경
        if (req.getTheaterId() != null) {
            Theater theater = getTheaterEntity(req.getTheaterId());
            screen.changeTheater(theater);
        }

        // 상영관 이름 변경
        if (req.getScreenName() != null) {
            screen.changeScreenName(req.getScreenName());
        }

        // 수용 인원 변경
        if (req.getCapacity() != null) {
            screen.changeCapacity(req.getCapacity());
        }
    }

    /**
     * 상영관 정보 수정 (관리자)
     *
     * 허용 상태:
     * - ACTIVE / HIDDEN / CLOSED: 수정 허용
     *
     * 금지 상태:
     * - DRAFT: 수정 불가 (draft 저장 API로만 수정 허용)
     * - DELETED: 수정 불가
     *
     * 수정 방식:
     * - 부분 수정(Partial Update)
     * - 요청 DTO에서 null로 전달된 필드는 "변경하지 않음"으로 처리
     *
     * 검증 책임:
     * - 상태 검증: ensureUpdatableStatus()
     * - 정책성 검증(영화관 존재 여부, 수용 인원 범위 등): 서비스 레이어
     */
    @Transactional
    public void update(Long screenId, ScreenUpdateAdminRequestDto req) {
        Screen screen = getEntity(screenId); // NotFound + DELETED 방어
        ensureUpdatableStatus(screen);

        // 소속 영화관 변경
        if (req.getTheaterId() != null) {
            Theater theater = theaterRepository.findById(req.getTheaterId())
                    .orElseThrow(() -> new NotFoundException("영화관을 찾을 수 없습니다."));
            screen.changeTheater(theater);
        }

        // 상영관 이름 변경
        if (req.getScreenName() != null) {
            screen.changeScreenName(req.getScreenName());
        }

        // 수용 인원 변경
        if (req.getCapacity() != null) {
            screen.changeCapacity(req.getCapacity()); // 엔티티에서 0 이상 검증
        }
    }

    /**
     * 상영관 완료 처리 (관리자)
     *
     * - DRAFT 상태의 상영관을 운영 가능한 상태(ACTIVE)로 전환
     * - 완료 시 필수값이 모두 유효해야 함(검증은 DTO + 엔티티 메서드에서)
     */
    @Transactional
    public void complete(Long screenId, ScreenCompleteAdminRequestDto req) {
        Screen screen = getEntity(screenId);

        if (screen.getStatus() != ScreenStatus.DRAFT) {
            throw new ConflictException("초안 상태(DRAFT)에서만 완료 처리가 가능합니다.");
        }

        // 필수값 세팅 (엔티티 도메인 메서드가 값 검증 수행)
        Theater theater = getTheaterEntity(req.getTheaterId());
        screen.changeTheater(theater);
        screen.changeScreenName(req.getScreenName());
        screen.changeCapacity(req.getCapacity());

        // 상태 전이
        screen.changeScreenStatus(ScreenStatus.ACTIVE);
    }

    /**
     * 상영관 운영중(ACTIVE) 전환 (관리자)
     * - 숨김(HIDDEN)/종료(CLOSED) 상태에서 복구 용도
     */
    @Transactional
    public void activate(Long screenId) {
        Screen screen = getEntity(screenId);

        if (screen.getStatus() == ScreenStatus.DRAFT) {
            throw new ConflictException("초안 상태(DRAFT)는 activate 대상이 아닙니다. complete를 진행하세요.");
        }

        screen.changeScreenStatus(ScreenStatus.ACTIVE);
    }

    /**
     * 상영관 숨김(HIDDEN) 전환 (관리자)
     */
    @Transactional
    public void hide(Long screenId) {
        Screen screen = getEntity(screenId);

        if (screen.getStatus() == ScreenStatus.DRAFT) {
            throw new ConflictException("초안 상태(DRAFT)는 hide 대상이 아닙니다.");
        }

        screen.changeScreenStatus(ScreenStatus.HIDDEN);
    }

    /**
     * 상영관 운영 종료(CLOSED) 전환 (관리자)
     */
    @Transactional
    public void close(Long screenId) {
        Screen screen = getEntity(screenId);

        if (screen.getStatus() == ScreenStatus.DRAFT) {
            throw new ConflictException("초안 상태(DRAFT)는 close 대상이 아닙니다.");
        }

        screen.changeScreenStatus(ScreenStatus.CLOSED);
    }

    /**
     * 상영관 삭제(소프트 삭제, DELETED) (관리자)
     */
    @Transactional
    public void remove(Long screenId) {
        Screen screen = getEntity(screenId);
        screen.markDeleted();
    }

    /* ======================= Helpers ======================= */

    /**
     * 상영관 수정 가능 상태 검증
     */
    private void ensureUpdatableStatus(Screen screen) {
        if (screen.getStatus() == ScreenStatus.DELETED) {
            throw new ConflictException("삭제된 상영관은 수정할 수 없습니다.");
        }
        if (screen.getStatus() == ScreenStatus.DRAFT) {
            throw new ConflictException("초안 상태의 상영관은 draft 저장을 통해 수정하세요.");
        }
    }

    /**
     * 상영관 엔티티 조회 (공통)
     *
     * - 존재하지 않으면 404
     * - DELETED 상태는 조회 불가(정책)로 처리
     */
    private Screen getEntity(Long screenId) {
        Screen screen = screenRepository.findById(screenId)
                .orElseThrow(() -> new NotFoundException("상영관을 찾을 수 없습니다."));

        if (screen.getStatus() == ScreenStatus.DELETED) {
            throw new NotFoundException("상영관을 찾을 수 없습니다.");
        }

        return screen;
    }

    /**
     * 영화관 조회 (공통)
     * - 존재하지 않으면 404
     * - DELETED는 정책상 사용 불가로 404 처리
     */
    private Theater getTheaterEntity(Long theaterId) {
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new NotFoundException("영화관을 찾을 수 없습니다."));

        // Theater도 status 기반 소프트 삭제라면, 여기서 방어(프로젝트 패턴대로)
        if (theater.getStatus() == TheaterStatus.DELETED) {
            throw new NotFoundException("영화관을 찾을 수 없습니다.");
        }

        return theater;
    }

    private ScreenListItemAdminResponseDto toListItemDto(Screen screen) {
        return ScreenListItemAdminResponseDto.builder()
                .screenId(screen.getScreenId())
                .theaterId(screen.getTheater() != null ? screen.getTheater().getTheaterId() : null)
                .screenName(screen.getScreenName())
                .capacity(screen.getCapacity())
                .status(screen.getStatus())
                .build();
    }

    private ScreenDetailAdminResponseDto toDetailDto(Screen screen) {
        return ScreenDetailAdminResponseDto.builder()
                .screenId(screen.getScreenId())
                .theaterId(screen.getTheater() != null ? screen.getTheater().getTheaterId() : null)
                .screenName(screen.getScreenName())
                .capacity(screen.getCapacity())
                .status(screen.getStatus())
                .build();
    }
}
