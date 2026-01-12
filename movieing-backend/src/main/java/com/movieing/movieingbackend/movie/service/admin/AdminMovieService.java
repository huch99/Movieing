package com.movieing.movieingbackend.movie.service.admin;

import com.movieing.movieingbackend.common.exception.BadRequestException;
import com.movieing.movieingbackend.common.exception.ConflictException;
import com.movieing.movieingbackend.common.exception.NotFoundException;
import com.movieing.movieingbackend.movie.dto.admin.*;
import com.movieing.movieingbackend.movie.entity.Movie;
import com.movieing.movieingbackend.movie.entity.MovieStatus;
import com.movieing.movieingbackend.movie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminMovieService {

    private final MovieRepository movieRepository;

    /* =========================
       Create (기본 DRAFT)
       ========================= */
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
        movie.saveDraft();
        validateDateRangeIfBothPresent(movie.getReleaseDate(), movie.getEndDate());

        return movieRepository.save(movie).getMovieId();
    }

    /* =========================
       Draft Save (임시 저장)
       - 정책: DRAFT에서만 허용
       ========================= */
    @Transactional
    public void saveDraft(Long movieId, MovieDraftSaveAdminRequestDto requestDto) {
        Movie movie = getMovie(movieId);

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
        movie.saveDraft();
        validateDateRangeIfBothPresent(movie.getReleaseDate(), movie.getEndDate());
    }

    /* =========================
       Complete (완료 버튼)
       - DRAFT -> COMING_SOON
       - 필수값 + 날짜 검증
       ========================= */
    @Transactional
    public void complete(Long movieId, MovieCompleteAdminRequestDto requestDto) {
        Movie movie = getMovie(movieId);

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

        validateRequiredForComplete(movie);
        validateDateRangeStrict(movie.getReleaseDate(), movie.getEndDate());

        // 엔티티 전이 규칙 (DRAFT -> COMING_SOON)
        movie.complete();
    }

    /* =========================
       Update (상세 수정)
       - 정책: DELETED는 수정 불가
       - 정책은 필요하면 더 강화 가능
       ========================= */
    @Transactional
    public void update(Long movieId, MovieUpdateAdminRequestDto requestDto) {
        Movie movie = getMovie(movieId);

        if (movie.getStatus() == MovieStatus.DELETED) {
            throw new ConflictException("삭제된 영화는 수정할 수 없습니다.");
        }

        // 예: ENDED 수정 막고 싶으면
        // if (movie.getStatus() == MovieStatus.ENDED) throw new ConflictException("종료된 영화는 수정할 수 없습니다.");

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

    /* =========================
       Admin status controls (선택 기능)
       - HIDDEN 토글/복구 같은 운영 기능
       ========================= */
    @Transactional
    public void hide(Long movieId) {
        Movie movie = getMovie(movieId);

        if (movie.getStatus() == MovieStatus.DELETED) {
            throw new ConflictException("삭제된 영화는 숨김 처리할 수 없습니다.");
        }

        movie.hide(); // 엔티티에 hide() 구현되어 있어야 함
    }

    @Transactional
    public void unhideToComingSoon(Long movieId) {
        Movie movie = getMovie(movieId);

        if (movie.getStatus() != MovieStatus.HIDDEN) {
            throw new ConflictException("HIDDEN 상태에서만 복구할 수 있습니다.");
        }

        // 운영 정책: 숨김 해제하면 COMING_SOON으로 복귀 (원하면 NOW_SHOWING 복귀 등 분기 가능)
        movie.setCommingSoon(); // setStatus 없으면 엔티티에 메서드 추가
    }

    /* =========================
       Delete (Soft delete)
       ========================= */
    @Transactional
    public void delete(Long movieId) {
        Movie movie = getMovie(movieId);

        if (movie.getStatus() == MovieStatus.DELETED) return;

        // 예: NOW_SHOWING 삭제 막고 싶으면
        // if (movie.getStatus() == MovieStatus.NOW_SHOWING) throw new ConflictException("상영 중인 영화는 삭제할 수 없습니다.");

        movie.softDelete();
    }

    /* =========================
       Read (Admin)
       ========================= */

    @Transactional(readOnly = true)
    public MovieDetailAdminResponseDto getDetail(Long movieId) {
        return MovieDetailAdminResponseDto.from(getMovie(movieId));
    }

    @Transactional(readOnly = true)
    public List<MovieListItemAdminResponseDto> getList() {
        return movieRepository.findByStatusNot(MovieStatus.DELETED)
                .stream()
                .map(MovieListItemAdminResponseDto::from)
                .toList();
    }

     /* =========================
       helpers
       ========================= */

    private Movie getMovie(Long movieId) {
        return movieRepository.findById(movieId)
                .orElseThrow(() -> new NotFoundException("영화를 찾을 수 없습니다. id=" + movieId));
    }

    private void validateDateRangeIfBothPresent(LocalDate releaseDate, LocalDate endDate) {
        if (releaseDate == null || endDate == null) return;
        if (endDate.isBefore(releaseDate)) {
            throw new BadRequestException("상영 종료일은 개봉일보다 빠를 수 없습니다.");
        }
    }

    private void validateDateRangeStrict(LocalDate releaseDate, LocalDate endDate) {
        if (releaseDate == null || endDate == null) {
            throw new BadRequestException("개봉일/상영 종료일은 필수입니다.");
        }
        if (endDate.isBefore(releaseDate)) {
            throw new BadRequestException("상영 종료일은 개봉일보다 빠를 수 없습니다.");
        }
    }

    private void validateRequiredForComplete(Movie movie) {
        if (isBlank(movie.getTitle())) throw new BadRequestException("제목은 필수입니다.");
        if (isBlank(movie.getSynopsis())) throw new BadRequestException("줄거리는 필수입니다.");
        if (movie.getRuntimeMin() == null || movie.getRuntimeMin() < 1)
            throw new BadRequestException("상영 시간(분)은 필수이며 1 이상이어야 합니다.");
        if (movie.getReleaseDate() == null) throw new BadRequestException("개봉일은 필수입니다.");
        if (movie.getEndDate() == null) throw new BadRequestException("상영 종료일은 필수입니다.");
        if (isBlank(movie.getRating())) throw new BadRequestException("관람등급은 필수입니다.");
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }


}
