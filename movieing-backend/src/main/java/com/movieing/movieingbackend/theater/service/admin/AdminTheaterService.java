package com.movieing.movieingbackend.theater.service.admin;

import com.movieing.movieingbackend.common.exception.ConflictException;
import com.movieing.movieingbackend.common.exception.NotFoundException;
import com.movieing.movieingbackend.theater.dto.admin.TheaterCompleteAdminRequestDto;
import com.movieing.movieingbackend.theater.dto.admin.TheaterDetailAdminResponseDto;
import com.movieing.movieingbackend.theater.dto.admin.TheaterDraftSaveAdminRequestDto;
import com.movieing.movieingbackend.theater.dto.admin.TheaterListItemAdminResponseDto;
import com.movieing.movieingbackend.theater.entity.Theater;
import com.movieing.movieingbackend.theater.entity.TheaterStatus;
import com.movieing.movieingbackend.theater.repository.TheaterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminTheaterService {

    private final TheaterRepository theaterRepository;

    @Transactional(readOnly = true)
    public List<TheaterListItemAdminResponseDto> getList() {
        return theaterRepository.findAllByStatusNot(TheaterStatus.DELETED)
                .stream()
                .map(t -> TheaterListItemAdminResponseDto.builder()
                        .theaterId(t.getTheaterId())
                        .theaterName(t.getTheaterName())
                        .address(t.getAddress())
                        .lat(t.getLat())
                        .lng(t.getLng())
                        .openTime(t.getOpenTime())
                        .closeTime(t.getCloseTime())
                        .status(t.getStatus())
                        .build()
                ).toList();
    }

    /**,
     * "초안" 생성
     */
    public Long createDraft() {
        Theater t = Theater.builder()
                .status(TheaterStatus.DRAFT)
                .build();
        theaterRepository.save(t);
        return t.getTheaterId();
    }

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

    public void activate(Long theaterId) {
        Theater t = getEntity(theaterId);
        if (t.getStatus() == TheaterStatus.DELETED) throw new NotFoundException("삭제된 영화관입니다.");
        t.changeTheaterStatus(TheaterStatus.ACTIVE);
    }

    public void hide(Long theaterId) {
        Theater t = getEntity(theaterId);
        if (t.getStatus() == TheaterStatus.DELETED) throw new NotFoundException("삭제된 영화관입니다.");
        t.changeTheaterStatus(TheaterStatus.HIDDEN);
    }

    public void close(Long theaterId) {
        Theater t = getEntity(theaterId);
        if (t.getStatus() == TheaterStatus.DELETED) throw new NotFoundException("삭제된 영화관입니다.");
        t.changeTheaterStatus(TheaterStatus.CLOSED);
    }

    public void remove(Long theaterId) {
        Theater t = getEntity(theaterId);
        t.changeTheaterStatus(TheaterStatus.DELETED);
    }

    private Theater getEntity(Long theaterId) {
        return theaterRepository.findById(theaterId)
                .orElseThrow(() -> new NotFoundException("영화관을 찾을 수 없습니다. id=" + theaterId));
    }
}
