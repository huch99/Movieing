import React, { useEffect, useMemo, useState } from 'react';
import './AdminMovieListPage.css';
import { useNavigate } from 'react-router-dom';
import { adminMovieApi } from './adminMovieApi';

const STATUS_OPTIONS = [
    { value: "ALL", label: "전체" },
    { value: "DRAFT", label: "임시 저장" },
    { value: "COMMING_SOON", label: "개봉 전" },
    { value: "NOW_SHOWING", label: "상영 중" },
    { value: "HIDDEN", label: "숨김" },
    { value: "ENDED", label: "상영 종료" },
    { value: "DELETED", label: "삭제 됨" },
];

const AdminMovieListPage = () => {
    const navigate = useNavigate();

    const [page, setPage] = useState(0);
    const [totalPage, setTotalPage] = useState(0);

    const [items, setItems] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const [stats, setStats] = useState({
        totalMovies: 0,
        showingMovies: 0,
        draftMovies: 0,
        endedMovies: 0,
        hiddenMovies: 0,
        topBookedMovie: null,
        topBookedMovieCount: 0,
        topRevenueMovie: null,
        topRevenueMovieAmount: 0,
        todayBookedMovies: 0,
        endingSoonMovies: 0,
    });
    const [statsLoading, setStatsLoading] = useState(false);
    const [statsError, setStatsError] = useState(null);

    // 🔹 필터 상태
    const [status, setStatus] = useState("ALL");
    const [keywords, setKeywords] = useState("");
    const [currentKeywords, setCurrentKeywords] = useState("");

    const load = async (nextPage = page) => {
        setLoading(true);
        setError(null);
        try {
            const params = {
                page: nextPage,
                size: 20,
                sort: 'createdAt,desc',
                ...(status && status !== "ALL" ? { status } : {}),
                ...(keywords?.trim() ? { keywords: keywords.trim() } : {})
            }
            const page = await adminMovieApi.getList(params);
            setItems(page?.content ?? []);
            setTotalPage(page.totalPages);
        } catch (e) {
            console.error(e);
            setError(e?.response?.data?.resultMessage ?? "목록 조회 실패");
        } finally {
            setLoading(false);
        }
    };

    const statsLoad = async () => {
        setStatsLoading(true);
        try {
            const data = await adminMovieApi.getStats();
            setStats(data);
        } catch (e) {
            console.error(e);
            setStatsError(e.message);
        } finally {
            setStatsLoading(false);
        }
    };

    useEffect(() => {
        load();
    }, [status, keywords]);

    useEffect(() => {
        statsLoad();
    }, []);

    const onCreateDraft = async () => {
        try {
            const movieId = await adminMovieApi.createDraft();
            navigate(`/admin/movies/${movieId}`);
        } catch (e) {
            alert(e?.response?.data?.resultMessage ?? e?.message ?? "초안 생성 실패");
        }
    };

    // 페이징 기능 - 이전 페이지
    const goPrev = () => {
        const next = Math.max(0, page - 1);
        setPage(next);
        load(next);
    }

    // 페이징 기능 - 다음 페이지
    const goNext = () => {
        const next = Math.min(totalPage - 1, page + 1);
        setPage(next);
        load(next);
    };

    if (loading) return <div>로딩중...</div>;
    if (error) return <div>에러: {error}</div>;

    return (
        <div className="admin-movie-list">
            <div className="admin-movie-list__card">
                <div className="admin-movie-list__top">
                    <div className="admin-movie-list__title-wrap">
                        <h2 className="admin-movie-list__title">영화 관리</h2>
                        <p className="admin-movie-list__subtitle">
                            초안 생성 → 임시 저장 → 완료(개봉예정) 흐름으로 관리하세요.
                        </p>
                    </div>

                    <div className="admin-movie-list__actions">
                        <button className="admin-movie-list__create-btn" onClick={onCreateDraft}>
                            영화(초안) 등록
                        </button>
                    </div>
                </div>

                <div className="admin-movie-list__stats">
                    <div className="admin-movie-list__stats-table-wrap">
                        <table className="admin-movie-list__stats-table">
                            <thead className="admin-movie-list__stats-thead">
                                <tr>
                                    <th>등록된 영화</th>
                                    <th>상영중인 영화</th>
                                    <th>작성중인 영화</th>
                                    <th>상영 종료 영화</th>
                                    <th>숨김 처리된 영화</th>
                                </tr>
                            </thead>

                            {statsError ? (
                                <tbody>
                                    <tr>
                                        <td className="admin-movie-list__empty" colSpan={5}>에러 {statsError}</td>
                                    </tr>
                                </tbody>
                            ) : (statsLoading ? (
                                <tbody>
                                    <tr>
                                        <td className="admin-movie-list__empty" colSpan={5}>로딩중...</td>
                                    </tr>
                                </tbody>
                            ) : (
                                <tbody className="admin-movie-list__stats-tbody">
                                    <tr>
                                        <td>{stats.totalMovies}</td>
                                        <td>{stats.showingMovies}</td>
                                        <td>{stats.draftMovies}</td>
                                        <td>{stats.endedMovies}</td>
                                        <td>{stats.hiddenMovies}</td>
                                    </tr>
                                </tbody>
                            ))}
                        </table>
                    </div>
                </div>

                <div className="admin-movie-list__stats">
                    <div className="admin-movie-list__stats-table-wrap">
                        <table className="admin-movie-list__stats-table">
                            <thead className="admin-movie-list__stats-thead">
                                <tr>
                                    <th>누적 예매 Top</th>
                                    <th>매출 Top</th>
                                    <th>오늘 예매 발생 영화</th>
                                    <th>상영기간 만료 임박</th>
                                </tr>
                            </thead>

                            {statsError ? (
                                <tbody>
                                    <tr>
                                        <td className="admin-movie-list__empty" colSpan={4}>에러 {statsError}</td>
                                    </tr>
                                </tbody>
                            ) : (statsLoading ? (
                                <tbody>
                                    <tr>
                                        <td className="admin-movie-list__empty" colSpan={4}>로딩 중...</td>
                                    </tr>
                                </tbody>
                            ) : (
                                <tbody className="admin-movie-list__stats-tbody">
                                    <tr>
                                        <td>{stats.topBookedMovie} - {stats.topBookedMovieCount}</td>
                                        <td>{stats.topRevenueMovie} - {stats.topRevenueMovieAmount}원</td>
                                        <td>{stats.todayBookedMovies}</td>
                                        <td>{stats.endingSoonMovies}</td>
                                    </tr>
                                </tbody>
                            ))}
                        </table>
                    </div>
                </div>

                {/* ===== Filters ===== */}
                <div className="admin-movie-list__filters">
                    <div className="admin-movie-list__filter">
                        <label>상태</label>
                        <select
                            value={status}
                            onChange={(e) => setStatus(e.target.value)}
                        >
                            {STATUS_OPTIONS.map((o) => (
                                <option key={o.value} value={o.value}>
                                    {o.label}
                                </option>
                            ))}
                        </select>
                    </div>

                    <div className="admin-movie-list__filter admin-movie-list__filter--grow">
                        <label>검색</label>
                        <form onSubmit={(e) => {
                            e.preventDefault();
                            setKeywords(currentKeywords.trim());
                            setCurrentKeywords("");
                        }}>
                            <input
                                value={currentKeywords}
                                onChange={(e) => setCurrentKeywords(e.target.value)}
                                placeholder="영화 제목 / ID"
                            />
                        </form>
                    </div>

                    <div className="admin-movie-list__count">
                        총 <strong>{items.length}</strong>개
                    </div>
                </div>

                <div className="admin-movie-list__table-wrap">
                    <table className="admin-movie-list__table">
                        <thead className="admin-movie-list__thead">
                            <tr>
                                <th>ID</th>
                                <th>제목</th>
                                <th>개봉일</th>
                                <th>종료일</th>
                                <th>상태</th>
                            </tr>
                        </thead>

                        <tbody className="admin-movie-list__tbody">
                            {items.length === 0 ? (
                                <tr>
                                    <td className="admin-movie-list__empty" colSpan={5}>
                                        검색 결과가 없습니다.
                                    </td>
                                </tr>
                            ) : (
                                items.map((m) => (
                                    <tr
                                        key={m.movieId}
                                        className="admin-movie-list__row"
                                        onClick={() => navigate(`/admin/movies/${m.movieId}`)}
                                    >
                                        <td className="admin-movie-list__id">{m.movieId}</td>

                                        <td className="admin-movie-list__title-cell">
                                            {m.title ? m.title : (
                                                <span className="admin-movie-list__muted">(제목 없음)</span>
                                            )}
                                        </td>

                                        <td className="admin-movie-list__date">{m.releaseDate ?? "-"}</td>
                                        <td className="admin-movie-list__date">{m.endDate ?? "-"}</td>

                                        <td className="admin-movie-list__status">
                                            <span className={`admin-movie-list__badge admin-movie-list__badge--${m.status}`}>
                                                <span className="admin-movie-list__dot" />
                                                {m.status}
                                            </span>
                                        </td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
                </div>

                <div className="pager">
                    <button onClick={goPrev} disabled={loading || page <= 0}>
                        이전
                    </button>
                    <div className="pager__info">
                        {totalPage > 0 ? `${page + 1} / ${totalPage}` : "-"}
                    </div>
                    <button
                        onClick={goNext}
                        disabled={loading || page + 1 >= totalPage}
                    >
                        다음
                    </button>
                </div>
            </div>
        </div>
    );
};

export default AdminMovieListPage;