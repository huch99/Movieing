import React, { useEffect, useMemo, useState } from 'react';
import './AdminMovieDetailPage.css';
import { useNavigate, useParams } from 'react-router-dom';
import { adminMovieApi } from './adminMovieApi';

const AdminMovieDetailPage = () => {
    const navigate = useNavigate();
    const { movieId } = useParams();
    const id = useMemo(() => Number(movieId), [movieId]);

    const [movie, setMovie] = useState(null);
    const [form, setForm] = useState({});
    const [loading, setLoading] = useState(true);

    const RATING_OPTIONS = ["전체이용가", "12세", "15세", "18세"];
    const GENRE_OPTIONS = [
        "액션",
        "드라마",
        "코미디",
        "로맨스",
        "스릴러",
        "공포",
        "SF",
        "판타지",
        "애니메이션",
        "다큐멘터리",
    ];

    const load = async () => {
        setLoading(true);
        try {
            const detail = await adminMovieApi.getDetail(id); // ✅ unwrap된 data가 온다고 가정

            setMovie(detail);
            setForm({
                title: detail?.title ?? "",
                synopsis: detail?.synopsis ?? "",
                director: detail?.director ?? "",
                genre: detail?.genre ?? "",
                runtimeMin: detail?.runtimeMin ?? "",
                releaseDate: detail?.releaseDate ?? "",
                endDate: detail?.endDate ?? "",
                rating: detail?.rating ?? "",
                posterUrl: detail?.posterUrl ?? "",
            });
        } catch (e) {
            alert(e?.response?.data?.resultMessage ?? e?.message ?? "상세 조회 실패");
            navigate("/admin/movies", { replace: true });
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (!movieId) return;
        if (!Number.isFinite(id) || id <= 0) {
            navigate("/admin/movies", { replace: true });
            return;
        }
        load();
    }, [id, movieId]);

    const onChange = (k, v) => {
        setForm((prev) => ({ ...prev, [k]: v }));
    };

    const onSaveDraft = async () => {
        try {
            await adminMovieApi.saveDraft(id, form);
            alert("임시 저장 완료");
            await load();
        } catch (e) {
            alert(e?.response?.data?.resultMessage ?? e?.message ?? "임시 저장 실패");
        }
    };

    const onComplete = async () => {
        try {
            const rm = Number(form.runtimeMin);
            if (!Number.isFinite(rm) || rm <= 0) {
                alert("상영시간(분)을 올바르게 입력하세요.");
                return;
            }

            const body = {
                movieId: id,
                status: movie.status,
                title: String(form.title ?? "").trim(),
                synopsis: String(form.synopsis ?? "").trim(),
                releaseDate: String(form.releaseDate ?? "").trim(),
                endDate: String(form.endDate ?? "").trim(),
                runtimeMin: rm,
                rating: String(form.rating ?? "").trim(),
                director: String(form.director ?? "").trim() || undefined,
                genre: String(form.genre ?? "").trim() || undefined,
                posterUrl: String(form.posterUrl ?? "").trim() || undefined,
            };

            await adminMovieApi.complete(id, body);
            alert("완료 처리(개봉예정) 완료");
            await load();
        } catch (e) {
            alert(e?.response?.data?.resultMessage ?? e?.message ?? "완료 처리 실패");
        }
    };

    const onUpdate = async () => {
        try {
            const body = {
                title: String(form.title ?? "").trim() || undefined,
                synopsis: String(form.synopsis ?? "").trim() || undefined,
                director: String(form.director ?? "").trim() || undefined,
                genre: String(form.genre ?? "").trim() || undefined,
                posterUrl: String(form.posterUrl ?? "").trim() || undefined,
                rating: String(form.rating ?? "").trim() || undefined,
                releaseDate: form.releaseDate || undefined,
                endDate: form.endDate || undefined,
                runtimeMin:
                    form.runtimeMin === "" || form.runtimeMin == null
                        ? undefined
                        : Number(form.runtimeMin),
            };

            await adminMovieApi.update(id, body);
            alert("수정 완료");
            await load();
        } catch (e) {
            alert(e?.response?.data?.resultMessage ?? e?.message ?? "수정 실패");
        }
    };

    const onDelete = async () => {
        if (!confirm("삭제(소프트 삭제) 하시겠습니까?")) return;
        try {
            await adminMovieApi.remove(id);
            alert("삭제 완료");
            navigate("/admin/movies", { replace: true });
        } catch (e) {
            alert(e?.response?.data?.resultMessage ?? e?.message ?? "삭제 실패");
        }
    };

    if (loading) return <div>로딩중...</div>;
    if (!movie) return null;

    const isDraft = movie.status === "DRAFT";

    return (
        <div className="admin-movie-detail">
            <div className="admin-movie-detail__card">
                <div className="admin-movie-detail__top">
                    <div className="admin-movie-detail__title-wrap">
                        <h2 className="admin-movie-detail__title">영화 상세</h2>

                        <div className="admin-movie-detail__meta">
                            <span className="admin-movie-detail__id">#{movie.movieId}</span>
                            <span
                                className={`admin-movie-detail__badge admin-movie-detail__badge--${movie.status}`}
                                title="현재 상태"
                            >
                                <span className="admin-movie-detail__dot" />
                                {movie.status}
                            </span>
                        </div>
                    </div>

                    <button
                        className="admin-movie-detail__back-btn"
                        onClick={() => navigate("/admin/movies")}
                        type="button"
                    >
                        목록
                    </button>
                </div>

                <div className="admin-movie-detail__content">
                    <h3 className="admin-movie-detail__section-title">기본 정보</h3>

                    <div className="admin-movie-detail__grid">
                        <div className="admin-movie-detail__field">
                            <div className="admin-movie-detail__label">제목</div>
                            <input
                                className="admin-movie-detail__input"
                                value={String(form.title ?? "")}
                                onChange={(e) => onChange("title", e.target.value)}
                            />
                        </div>

                        <div className="admin-movie-detail__field">
                            <div className="admin-movie-detail__label">감독</div>
                            <input
                                className="admin-movie-detail__input"
                                value={String(form.director ?? "")}
                                onChange={(e) => onChange("director", e.target.value)}
                            />
                        </div>

                        <div className="admin-movie-detail__field">
                            <div className="admin-movie-detail__label">장르</div>
                            <select
                                className="admin-movie-detail__select"
                                value={String(form.genre ?? "")}
                                onChange={(e) => onChange("genre", e.target.value)}
                            >
                                <option value="" disabled>
                                    선택하세요
                                </option>
                                {GENRE_OPTIONS.map((g) => (
                                    <option key={g} value={g}>
                                        {g}
                                    </option>
                                ))}
                            </select>
                        </div>

                        <div className="admin-movie-detail__field">
                            <div className="admin-movie-detail__label">관람등급</div>
                            <select
                                className="admin-movie-detail__select"
                                value={String(form.rating ?? "")}
                                onChange={(e) => onChange("rating", e.target.value)}
                            >
                                <option value="" disabled>
                                    선택하세요
                                </option>
                                {RATING_OPTIONS.map((r) => (
                                    <option key={r} value={r}>
                                        {r}
                                    </option>
                                ))}
                            </select>
                        </div>

                        <div className="admin-movie-detail__field">
                            <div className="admin-movie-detail__label">상영시간(분)</div>
                            <input
                                className="admin-movie-detail__input"
                                type="number"
                                value={form.runtimeMin ?? ""}
                                onChange={(e) => onChange("runtimeMin", e.target.value)}
                            />
                        </div>

                        <div className="admin-movie-detail__field">
                            <div className="admin-movie-detail__label">개봉일</div>
                            <input
                                className="admin-movie-detail__input"
                                type="date"
                                value={String(form.releaseDate ?? "")}
                                onChange={(e) => onChange("releaseDate", e.target.value)}
                            />
                        </div>

                        <div className="admin-movie-detail__field">
                            <div className="admin-movie-detail__label">종료일</div>
                            <input
                                className="admin-movie-detail__input"
                                type="date"
                                value={String(form.endDate ?? "")}
                                onChange={(e) => onChange("endDate", e.target.value)}
                            />
                        </div>

                        <div className="admin-movie-detail__field admin-movie-detail__field--full">
                            <div className="admin-movie-detail__label">포스터 URL</div>
                            <input
                                className="admin-movie-detail__input"
                                value={String(form.posterUrl ?? "")}
                                onChange={(e) => onChange("posterUrl", e.target.value)}
                            />
                        </div>

                        <div className="admin-movie-detail__field admin-movie-detail__field--full">
                            <div className="admin-movie-detail__label">줄거리</div>
                            <textarea
                                className="admin-movie-detail__textarea"
                                value={String(form.synopsis ?? "")}
                                onChange={(e) => onChange("synopsis", e.target.value)}
                            />
                        </div>
                    </div>

                    <div className="admin-movie-detail__actions">
                        <button
                            className="admin-movie-detail__btn admin-movie-detail__btn--ghost"
                            onClick={isDraft ? onSaveDraft : onUpdate}
                            type="button"
                        >
                            {isDraft ? "임시 저장" : "수정"}
                        </button>

                        <button
                            className="admin-movie-detail__btn admin-movie-detail__btn--primary"
                            onClick={onComplete}
                            disabled={!isDraft}
                            type="button"
                        >
                            완료(개봉예정 전환)
                        </button>

                        <button
                            className="admin-movie-detail__btn admin-movie-detail__btn--danger"
                            onClick={onDelete}
                            type="button"
                        >
                            삭제
                        </button>
                    </div>

                    {!isDraft && (
                        <div className="admin-movie-detail__hint">
                            * 완료 버튼은 DRAFT 상태에서만 가능합니다. (백엔드도 409 처리)
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default AdminMovieDetailPage;