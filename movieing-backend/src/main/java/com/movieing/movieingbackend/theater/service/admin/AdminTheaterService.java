package com.movieing.movieingbackend.theater.service.admin;

import com.movieing.movieingbackend.aspect.ApiResponse;
import com.movieing.movieingbackend.common.exception.ConflictException;
import com.movieing.movieingbackend.common.exception.NotFoundException;
import com.movieing.movieingbackend.screen.entity.ScreenStatus;
import com.movieing.movieingbackend.screen.repository.ScreenRepository;
import com.movieing.movieingbackend.seat.entity.SeatStatus;
import com.movieing.movieingbackend.seat.respository.SeatRepository;
import com.movieing.movieingbackend.theater.dto.admin.*;
import com.movieing.movieingbackend.theater.entity.Theater;
import com.movieing.movieingbackend.theater.entity.TheaterStatus;
import com.movieing.movieingbackend.theater.repository.TheaterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 영화관(Theater) 관리자 서비스
 *
 * - 관리자 기능: 목록/상세 조회, 초안 생성, 임시 저장, 완료 처리, 상태 전환(운영/숨김/종료/삭제)
 * - 초안(DRAFT) 상태에서만 수정(임시 저장) 및 완료 처리를 허용
 * - 삭제(DELETED) 상태는 조회/수정/상태 전환에서 예외 처리
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AdminTheaterService {

    private final TheaterRepository theaterRepository;
    private final ScreenRepository screenRepository;
    private final SeatRepository seatRepository;

    /**
     * 상태 조건으로 영화관 목록 조회 (Admin)
     * 스케줄 페이지에서 사용
     */
    @Transactional(readOnly = true)
    public Page<TheaterListItemAdminResponseDto> getListByStatuses(List<TheaterStatus> statuses, Pageable pageable) {
        return theaterRepository.findByStatusIn(statuses, pageable)
                .map(t -> TheaterListItemAdminResponseDto.builder()
                        .theaterId(t.getTheaterId())
                        .theaterName(t.getTheaterName())
                        .address(t.getAddress())
                        .status(t.getStatus())
                        .build()
                );
    }

    /**
     * 어드민 영화관 통계 조회
     */
    @Transactional(readOnly = true)
    public TheaterStatsAdminResponseDto getStats() {
        return TheaterStatsAdminResponseDto.builder()
                .totalTheaters(theaterRepository.countByStatusNot(TheaterStatus.DELETED))
                .activeTheaters(theaterRepository.countByStatus(TheaterStatus.ACTIVE))
                .totalScreens(screenRepository.countByStatusNot(ScreenStatus.DELETED))
                .activeScreens(screenRepository.countByStatus(ScreenStatus.ACTIVE))
                .totalSeats(seatRepository.count())
                .activeSeats(seatRepository.countByStatus(SeatStatus.ACTIVE))
                .build();
    }

    /**
     * 영화관 목록 조회 (관리자)
     * - 삭제(DELETED) 상태를 제외한 모든 영화관을 조회
     * - 관리자 영화관 관리 리스트 화면에서 사용
     */
    @Transactional(readOnly = true)
    public Page<TheaterListItemAdminResponseDto> getList(Pageable pageable, TheaterStatus status, String keywords) {
        String q = (keywords == null ? null : keywords.trim().toLowerCase());
        boolean hasKeywords = (q != null && !q.isBlank());

        Long id = null;
        if(hasKeywords && q.matches("^\\d+$")) {
            id = Long.valueOf(q);
        }

        Page<Theater> page;

        if(status == null) {
            if(!hasKeywords) {
                page = theaterRepository.findAllByStatusNot(TheaterStatus.DELETED, pageable);
            } else {
                page = theaterRepository.searchNotDeleted(TheaterStatus.DELETED, q.toLowerCase(), id, pageable);
            }
        } else {
            if(!hasKeywords) {
                page = theaterRepository.findByStatus(status, pageable);
            } else {
                page = theaterRepository.searchByStatus(status, q.toLowerCase(), id, pageable);
            }
        }
        return page.map(t -> TheaterListItemAdminResponseDto.builder()
                        .theaterId(t.getTheaterId())
                        .theaterName(t.getTheaterName())
                        .address(t.getAddress())
                        .status(t.getStatus())
                        .build()
                );
    }

    /**
     * 영화관 초안(DRAFT) 생성 (관리자)
     * - 최소 정보만 가진 DRAFT 상태의 영화관 레코드를 생성
     * - 생성된 theaterId를 반환하고, 이후 임시 저장/완료 흐름으로 진행
     */
    public Long createDraft() {
        Theater t = Theater.builder()
                .status(TheaterStatus.DRAFT)
                .build();
        theaterRepository.save(t);
        return t.getTheaterId();
    }

    /**
     * 영화관 상세 조회 (관리자)
     * - theaterId에 해당하는 영화관의 상세 정보를 반환
     */
    @Transactional(readOnly = true)
    public TheaterDetailAdminResponseDto getDetail(Long theaterId) {
        Theater t = getEntity(theaterId);

        return TheaterDetailAdminResponseDto.builder()
                .theaterId(t.getTheaterId())
                .status(t.getStatus())
                .theaterName(t.getTheaterName())
                .address(t.getAddress())
                .lat(t.getLat())
                .lng(t.getLng())
                .openTime(t.getOpenTime())
                .closeTime(t.getCloseTime())
                .build();
    }

    /**
     * 영화관 임시 저장(DRAFT 저장) (관리자)
     * - DRAFT 상태에서만 허용
     * - 입력된 값만 반영하는 부분 저장 방식 (null은 "변경하지 않음")
     */
    public void saveDraft(Long theaterId, TheaterDraftSaveAdminRequestDto req) {
        Theater t = getEntity(theaterId);

        if (t.getStatus() == TheaterStatus.DELETED) throw new NotFoundException("삭제된 영화관입니다.");
        if (t.getStatus() != TheaterStatus.DRAFT) throw new ConflictException("임시 저장은 DRAFT 상태에서만 가능합니다.");

        // null은 "변경하지 않음"으로 처리 (부분 저장)
        if (req.getTheaterName() != null) t.changeTheaterName(req.getTheaterName().trim());
        if (req.getAddress() != null) t.changeAddress(req.getAddress().trim());
        if (req.getLat() != null) t.changeLat(req.getLat());
        if (req.getLng() != null) t.changeLng(req.getLng());
        if (req.getOpenTime() != null) t.changeOpenTime(req.getOpenTime());
        if (req.getCloseTime() != null) t.changeCloseTime(req.getCloseTime());
    }

    /**
     * 영화관 완료 처리 (관리자)
     * - DRAFT 상태에서만 허용
     * - 완료 처리 시 필수값 검증은 DTO(@Valid)에서 1차 수행
     * - 서비스에서는 정책성 검증(예: 위경도 쌍 입력)을 추가로 수행
     * - 완료 후 상태는 ACTIVE로 전환
     */
    public void complete(Long theaterId, TheaterCompleteAdminRequestDto req) {
        Theater t = getEntity(theaterId);

        if (t.getStatus() == TheaterStatus.DELETED) throw new NotFoundException("삭제된 영화관입니다.");
        if (t.getStatus() != TheaterStatus.DRAFT) throw new ConflictException("완료 처리는 DRAFT 상태에서만 가능합니다.");

        // (추가 방어) 위경도는 둘 다 있거나 둘 다 없어야 함
        if ((req.getLat() == null) != (req.getLng() == null)) {
            throw new ConflictException("위경도(lat/lng)는 둘 다 입력하거나 둘 다 비워야 합니다.");
        }

        t.changeTheaterName(req.getTheaterName().trim());
        t.changeAddress(req.getAddress().trim());
        t.changeOpenTime(req.getOpenTime());
        t.changeCloseTime(req.getCloseTime());
        t.changeLat(req.getLat());
        t.changeLng(req.getLng());

        t.changeTheaterStatus(TheaterStatus.ACTIVE);
    }

    /**
     * 영화관 운영 상태(ACTIVE) 전환 (관리자)
     * - 숨김(HIDDEN) 또는 종료(CLOSED) 상태에서 재오픈 용도로 사용
     */
    public void activate(Long theaterId) {
        Theater t = getEntity(theaterId);
        if (t.getStatus() == TheaterStatus.DELETED) throw new NotFoundException("삭제된 영화관입니다.");
        t.changeTheaterStatus(TheaterStatus.ACTIVE);
    }

    /**
     * 영화관 숨김(HIDDEN) 전환 (관리자)
     * - 사용자 노출을 중지하고 싶을 때 사용
     */
    public void hide(Long theaterId) {
        Theater t = getEntity(theaterId);
        if (t.getStatus() == TheaterStatus.DELETED) throw new NotFoundException("삭제된 영화관입니다.");
        t.changeTheaterStatus(TheaterStatus.HIDDEN);
    }

    /**
     * 영화관 운영 종료(CLOSED) 전환 (관리자)
     * - 운영 종료 상태로 전환 (정책에 따라 노출/예매 제한)
     */
    public void close(Long theaterId) {
        Theater t = getEntity(theaterId);
        if (t.getStatus() == TheaterStatus.DELETED) throw new NotFoundException("삭제된 영화관입니다.");
        t.changeTheaterStatus(TheaterStatus.CLOSED);
    }

    /**
     * 영화관 삭제(소프트 삭제, DELETED) 처리 (관리자)
     * - 물리 삭제가 아닌 상태값 변경으로 삭제 처리
     */
    public void remove(Long theaterId) {
        Theater t = getEntity(theaterId);
        t.changeTheaterStatus(TheaterStatus.DELETED);
    }

    /**
     * theaterId로 Theater 엔티티 조회
     * - 존재하지 않으면 NotFoundException 발생
     */
    private Theater getEntity(Long theaterId) {
        return theaterRepository.findById(theaterId)
                .orElseThrow(() -> new NotFoundException("영화관을 찾을 수 없습니다. id=" + theaterId));
    }

    /**
     * 영화관 정보 수정 (관리자)
     *
     * 허용 상태:
     * - ACTIVE / HIDDEN / CLOSED: 수정 허용
     *
     * 금지 상태:
     * - DRAFT: draft 저장 API를 통해서만 수정 가능
     * - DELETED: 수정 불가
     *
     * 수정 방식:
     * - 부분 수정(Partial Update)
     * - 요청 DTO에서 null로 전달된 필드는 "변경하지 않음"으로 처리
     *
     * 검증 책임:
     * - 필수값 검증: 없음(모두 optional)
     * - 상태 검증: ensureUpdatableStatus()
     * - 정책성 검증(위경도 쌍, 시간 범위 등): 필요 시 서비스에서 추가
     */
    @Transactional
    public void update(Long theaterId, TheaterUpdateAdminRequestDto req) {
        Theater theater = getEntity(theaterId); // DELETED 방어
        ensureUpdatableStatus(theater);

        // 부분 변경 (null이면 변경 안 함)
        if (req.getTheaterName() != null) {
            theater.changeTheaterName(req.getTheaterName());
        }
        if (req.getAddress() != null) {
            theater.changeAddress(req.getAddress());
        }
        if (req.getLat() != null) {
            theater.changeLat(req.getLat());
        }
        if (req.getLng() != null) {
            theater.changeLng(req.getLng());
        }
        if (req.getOpenTime() != null) {
            theater.changeOpenTime(req.getOpenTime());
        }
        if (req.getCloseTime() != null) {
            theater.changeCloseTime(req.getCloseTime());
        }
    }

    /**
     * 영화관 수정 가능 상태 검증
     *
     * 정책:
     * - ACTIVE / HIDDEN / CLOSED: 수정 허용
     * - DRAFT: 수정 불가 (draft 저장 API로만 수정 허용)
     * - DELETED: 수정 불가
     */
    private void ensureUpdatableStatus(Theater theater) {
        if (theater.getStatus() == TheaterStatus.DELETED) {
            throw new ConflictException("삭제된 영화관은 수정할 수 없습니다.");
        }
        if (theater.getStatus() == TheaterStatus.DRAFT) {
            throw new ConflictException("초안 상태의 영화관은 draft 저장을 통해 수정하세요.");
        }
        // ACTIVE / HIDDEN / CLOSED 는 허용
    }
}
