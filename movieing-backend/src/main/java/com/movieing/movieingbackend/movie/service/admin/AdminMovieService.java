package com.movieing.movieingbackend.movie.service.admin;

import com.movieing.movieingbackend.booking_seat.repository.BookingSeatRepository;
import com.movieing.movieingbackend.common.exception.BadRequestException;
import com.movieing.movieingbackend.common.exception.ConflictException;
import com.movieing.movieingbackend.common.exception.NotFoundException;
import com.movieing.movieingbackend.movie.dto.admin.*;
import com.movieing.movieingbackend.movie.entity.Movie;
import com.movieing.movieingbackend.movie.entity.MovieStatus;
import com.movieing.movieingbackend.movie.repository.MovieRepository;
import com.movieing.movieingbackend.payment.entity.PaymentStatus;
import com.movieing.movieingbackend.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Admin 영화 관리 서비스
 *
 * 제공 기능:
 * - 영화 초안 생성/임시저장/완료처리/수정/숨김/복구/삭제/상세/목록 조회(Page)
 *
 * 예외 정책:
 * - 상태/업무 규칙 위반: ConflictException(409)
 * - 리소스 미존재: NotFoundException(404)
 * - 요청값 오류(날짜 역전 등): BadRequestException(400)
 */
@Service
@RequiredArgsConstructor
public class AdminMovieService {

    private final MovieRepository movieRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final PaymentRepository paymentRepository;

    /**
     * 상태 목록으로 영화 목록 조회
     *
     * <p>
     * Admin 영역에서 특정 상태의 영화만 조회할 때 사용한다.
     * 주로 스케줄 등록 시 선택 가능한 영화 목록을 구성하기 위해 사용된다.
     * </p>
     *
     * @param statuses 조회할 영화 상태 목록
     * @return 상태 조건에 맞는 영화 목록 DTO
     */
    public List<MovieListItemAdminResponseDto> getListByStatuses(List<MovieStatus> statuses) {
        return movieRepository.findByStatusIn(statuses).stream()
                .map(m -> MovieListItemAdminResponseDto.builder()
                        .movieId(m.getMovieId())
                        .title(m.getTitle())
                        .releaseDate(m.getReleaseDate())
                        .endDate(m.getEndDate())
                        .status(m.getStatus())
                        .posterUrl(m.getPosterUrl())
                        .build())
                .toList();
    }

    /**
     * 요청 파라미터로 전달된 상태 문자열을 MovieStatus 목록으로 변환
     *
     * <p>
     * - "COMMING_SOON,NOW_SHOWING" 형태의 문자열을 파싱한다.
     * - 파라미터가 없거나 비어있는 경우 기본 상태 목록을 반환한다.
     * - Admin 컨트롤러에서 공통 유틸 용도로 사용된다.
     * </p>
     *
     * @param raw 요청 파라미터 상태 문자열 (comma-separated)
     * @param defaultStatuses 파라미터가 없을 때 사용할 기본 상태 목록
     * @return 파싱된 MovieStatus 목록
     * @throws IllegalArgumentException 잘못된 상태 문자열이 전달된 경우
     */
    public List<MovieStatus> parseStatusesOrDefault(String raw, List<MovieStatus> defaultStatuses) {
        if (raw == null || raw.isBlank()) return defaultStatuses;

        List<String> tokens = Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();

        if (tokens.isEmpty()) return defaultStatuses;

        return tokens.stream()
                .map(MovieStatus::valueOf)
                .collect(Collectors.toList());
    }

    /**
     * 영화 초안(DRAFT) 생성
     *
     * - Draft 단계는 모든 필드가 optional
     * - 입력된 값만 저장하고 status는 DRAFT로 유지
     */
    @Transactional
    public Long createDraft(MovieDraftSaveAdminRequestDto requestDto) {
        Movie movie = Movie.builder()
                .title(requestDto.getTitle())
                .synopsis(requestDto.getSynopsis())
                .director(requestDto.getDirector())
                .genre(requestDto.getGenre())
                .runtimeMin(requestDto.getRuntimeMin())
                .releaseDate(requestDto.getReleaseDate())
                .endDate(requestDto.getEndDate())
                .rating(requestDto.getRating())
                .posterUrl(requestDto.getPosterUrl())
                .build();
        // 명시적으로 Draft 상태 유지 (Builder.Default로 DRAFT여도 의도 표현용)
        movie.saveDraft();
        // 날짜가 둘 다 있을 때만 범위 체크
        validateDateRangeIfBothPresent(movie.getReleaseDate(), movie.getEndDate());

        return movieRepository.save(movie).getMovieId();
    }

    /**
     * 영화 초안 임시 저장(부분 저장)
     *
     * 정책:
     * - DRAFT 상태에서만 허용
     * - 전달된 값만 부분 반영
     */
    @Transactional
    public void saveDraft(Long movieId, MovieDraftSaveAdminRequestDto requestDto) {

        Movie movie = getMovieActive(movieId); // DELETED 방어 포함

        if (movie.getStatus() != MovieStatus.DRAFT) {
            throw new ConflictException("임시 저장은 DRAFT 상태에서만 가능합니다.");
        }

        movie.updateDetail(
                requestDto.getTitle(),
                requestDto.getSynopsis(),
                requestDto.getDirector(),
                requestDto.getGenre(),
                requestDto.getRuntimeMin(),
                requestDto.getReleaseDate(),
                requestDto.getEndDate(),
                requestDto.getRating(),
                requestDto.getPosterUrl(),
                null
        );
        // Draft 유지
        movie.saveDraft();
        // 날짜가 둘 다 있을 때만 범위 체크
        validateDateRangeIfBothPresent(movie.getReleaseDate(), movie.getEndDate());
    }

    /**
     * 영화 완료 처리 (DRAFT -> COMMING_SOON)
     *
     * - DTO Validation(@Valid)로 필수값/형식 검증
     * - 서비스에서는 날짜 상호관계(releaseDate <= endDate) 같은 정책 검증 수행
     *
     * (보완) PathVariable movieId와 DTO movieId가 함께 온다면 불일치 방지 체크
     */
    @Transactional
    public void complete(Long movieId, MovieCompleteAdminRequestDto requestDto) {

        if (requestDto.getMovieId() != null && !movieId.equals(requestDto.getMovieId())) {
            throw new BadRequestException("movieId가 일치하지 않습니다.");
        }

        Movie movie = getMovieActive(movieId);

        if (movie.getStatus() != MovieStatus.DRAFT) {
            throw new ConflictException("완료 처리는 DRAFT 상태에서만 가능합니다.");
        }

        movie.updateDetail(
                requestDto.getTitle(),
                requestDto.getSynopsis(),
                requestDto.getDirector(),
                requestDto.getGenre(),
                requestDto.getRuntimeMin(),
                requestDto.getReleaseDate(),
                requestDto.getEndDate(),
                requestDto.getRating(),
                requestDto.getPosterUrl(),
                null
        );

        // 완료 단계는 날짜 필수 + 범위 엄격 검증
        validateDateRangeStrict(movie.getReleaseDate(), movie.getEndDate());

        // 도메인 전이 규칙 (DRAFT -> COMMING_SOON)
        movie.complete();
    }

    /**
     * 영화 정보 수정(부분 수정)
     *
     * 정책:
     * - DELETED 상태는 수정 불가
     * - 전달된 값만 부분 반영
     * - 날짜는 둘 다 있을 경우에만 범위 체크
     */
    @Transactional
    public void update(Long movieId, MovieUpdateAdminRequestDto requestDto) {
        Movie movie = getMovieActive(movieId);

        movie.updateDetail(
                requestDto.getTitle(),
                requestDto.getSynopsis(),
                requestDto.getDirector(),
                requestDto.getGenre(),
                requestDto.getRuntimeMin(),
                requestDto.getReleaseDate(),
                requestDto.getEndDate(),
                requestDto.getRating(),
                requestDto.getPosterUrl(),
                null
        );

        validateDateRangeIfBothPresent(movie.getReleaseDate(), movie.getEndDate());
    }

    /**
     * 영화 숨김 처리
     *
     * - 사용자 화면 노출을 막기 위한 운영 기능
     * - DELETED는 숨김 처리 불가
     */
    @Transactional
    public void hide(Long movieId) {
        Movie movie = getMovieActive(movieId);

        movie.hide();
    }

    /**
     * 숨김(HIDDEN) 영화 복구
     *
     * (보완) 복구 시 날짜 기반으로 상태를 더 자연스럽게 결정
     * - releaseDate <= today 이면 NOW_SHOWING으로 복구
     * - 아니면 COMMING_SOON으로 복구
     *
     * 주의:
     * - 엔티티에 HIDDEN -> NOW_SHOWING 직접 전이 메서드가 없다면 정책적으로 허용할지 결정 필요.
     * - 여기서는 "운영 기능"으로 허용한다는 가정 하에 status를 직접 설정하지 않고,
     *   가능한 경우 도메인 메서드를 활용하는 방식으로 구성.
     */
    @Transactional
    public void unhide(Long movieId) {
        Movie movie = getMovieActive(movieId);

        if (movie.getStatus() != MovieStatus.HIDDEN) {
            throw new ConflictException("HIDDEN 상태에서만 복구할 수 있습니다.");
        }

        LocalDate today = LocalDate.now();
        LocalDate releaseDate = movie.getReleaseDate();

        // 1) 기본 복구: COMMING_SOON
        movie.setCommingSoon(); // setStatus 없으면 엔티티에 메서드 추가

        // 2) 개봉일이 오늘 이전/오늘이면 NOW_SHOWING으로 한 번 더 전이
        if (releaseDate != null && !releaseDate.isAfter(today)) {
            movie.startShowing(); // COMMING_SOON -> NOW_SHOWING
        }
    }

    /**
     * 영화 소프트 삭제
     *
     * - 물리 삭제 대신 DELETED로 전환
     * - 이미 DELETED면 무시
     */
    @Transactional
    public void delete(Long movieId) {
        Movie movie = getMovie(movieId);

        if (movie.getStatus() == MovieStatus.DELETED) return;

        movie.softDelete();
    }

    /**
     * 영화 상세 조회 (Admin)
     *
     * (보완) DELETED는 "없는 리소스"처럼 처리하여 상세 조회에서 제외
     */
    @Transactional(readOnly = true)
    public MovieDetailAdminResponseDto getDetail(Long movieId) {
        Movie movie = getMovieActive(movieId);
        return MovieDetailAdminResponseDto.from(movie);
    }

    /**
     * 영화 목록 조회 (Admin)
     *
     * - DELETED 제외
     * - Page 기반 페이징/정렬 지원
     */
    @Transactional(readOnly = true)
    public Page<MovieListItemAdminResponseDto> getList(Pageable pageable) {
        return movieRepository.findByStatusNot(MovieStatus.DELETED, pageable)
                .map(MovieListItemAdminResponseDto::from);
    }

    /**
     * 영화 통계 조회
     * */
    @Transactional(readOnly = true)
    public MovieStatsAdminResponseDto getStats() {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        LocalDate endDate = today.plusDays(7);

        var statuses = List.of(MovieStatus.NOW_SHOWING, MovieStatus.COMMING_SOON);

        var top = bookingSeatRepository.findTopBookedMovie(
                PaymentStatus.PAID,
                statuses,
                PageRequest.of(0,1)
        ).stream().findFirst().orElse(null);

        var revenue = paymentRepository.findTopRevenueMovie(
                PaymentStatus.PAID,
                statuses,
                PageRequest.of(0, 1)
        ).stream().findFirst().orElse(null);

        Long cnt = paymentRepository.countTodayBookedMovies(
                PaymentStatus.PAID,
                start,
                end,
                statuses
        );

        Long endingSoon = movieRepository.countEndingSoonMovies(
                MovieStatus.NOW_SHOWING,
                today,
                endDate
        );

        return MovieStatsAdminResponseDto.builder()
                .totalMovies(movieRepository.countByStatusNot(MovieStatus.DELETED))
                .showingMovies(movieRepository.countByStatusIn(List.of(MovieStatus.NOW_SHOWING, MovieStatus.COMMING_SOON)))
                .draftMovies(movieRepository.countByStatus(MovieStatus.DRAFT))
                .endedMovies(movieRepository.countByStatus(MovieStatus.ENDED))
                .hiddenMovies(movieRepository.countByStatus(MovieStatus.HIDDEN))
                .topBookedMovie(top != null ? top.getTitle() : null)
                .topBookedMovieCount(top != null ? top.getSeatCount() : 0L)
                .topRevenueMovie(revenue != null ? revenue.getTitle() : null)
                .topRevenueMovieAmount(revenue != null ? revenue.getAmount() : 0.0)
                .todayBookedMovies(cnt != null ? cnt : 0L)
                .endingSoonMovies(endingSoon != null ? endingSoon : 0L)
                .build();
    }

    /**
     * 영화 단건 조회
     */
    private Movie getMovie(Long movieId) {
        return movieRepository.findById(movieId)
                .orElseThrow(() -> new NotFoundException("영화를 찾을 수 없습니다. id=" + movieId));
    }

    /**
     * (보완) DELETED는 Admin에서도 "없는 리소스"처럼 처리하고 싶을 때 사용하는 조회 헬퍼
     */
    private Movie getMovieActive(Long movieId) {
        Movie movie = getMovie(movieId);
        if (movie.getStatus() == MovieStatus.DELETED) {
            throw new NotFoundException("영화를 찾을 수 없습니다. id=" + movieId);
        }
        return movie;
    }

    /**
     * 날짜가 둘 다 있을 때만 날짜 범위를 검증 (Draft/Update 단계)
     */
    private void validateDateRangeIfBothPresent(LocalDate releaseDate, LocalDate endDate) {
        if (releaseDate == null || endDate == null) return;
        if (endDate.isBefore(releaseDate)) {
            throw new BadRequestException("상영 종료일은 개봉일보다 빠를 수 없습니다.");
        }
    }

    /**
     * 완료 처리 단계에서 날짜 필수 + 날짜 범위 엄격 검증
     */
    private void validateDateRangeStrict(LocalDate releaseDate, LocalDate endDate) {
        if (releaseDate == null || endDate == null) {
            throw new BadRequestException("개봉일/상영 종료일은 필수입니다.");
        }
        if (endDate.isBefore(releaseDate)) {
            throw new BadRequestException("상영 종료일은 개봉일보다 빠를 수 없습니다.");
        }
    }
}
