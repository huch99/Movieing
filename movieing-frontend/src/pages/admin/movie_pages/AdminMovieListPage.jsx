import React, { useEffect, useMemo, useState } from 'react';
import './AdminMovieListPage.css';
import { useNavigate } from 'react-router-dom';
import { adminMovieApi } from './adminMovieApi';

const STATUS_OPTIONS = [
    { value: "ALL", label: "전체" },
    { value: "DRAFT", label: "DRAFT" },
    { value: "COMING_SOON", label: "COMING_SOON" },
    { value: "NOW_SHOWING", label: "NOW_SHOWING" },
    { value: "HIDDEN", label: "HIDDEN" },
    { value: "ENDED", label: "ENDED" },
    { value: "DELETED", label: "DELETED" },
];

const AdminMovieListPage = () => {
    const nav = useNavigate();
    const [items, setItems] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // 🔹 필터 상태
    const [status, setStatus] = useState("ALL");
    const [q, setQ] = useState("");

    const load = async () => {
        setLoading(true);
        setError(null);
        try {
            const page = await adminMovieApi.getList({ page: 0, size: 20 });
            setItems(page?.content ?? []);
        } catch (e) {
            setError(e?.response?.data?.resultMessage ?? "목록 조회 실패");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        load();
    }, []);

    const filtered = useMemo(() => {
        const keyword = q.trim().toLowerCase();

        return items
            .filter((m) => {
                if (status === "ALL") return true;
                return m.status === status;
            })
            .filter((m) => {
                if (!keyword) return true;
                const hay = [
                    m.movieId,
                    m.title,
                ]
                    .filter(Boolean)
                    .join(" ")
                    .toLowerCase();
                return hay.includes(keyword);
            });
    }, [items, status, q]);

    const onCreateDraft = async () => {
        try {
            const movieId = await adminMovieApi.createDraft();
            nav(`/admin/movies/${movieId}`);
        } catch (e) {
            alert(e?.response?.data?.resultMessage ?? e?.message ?? "초안 생성 실패");
        }
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
                        <input
                            value={q}
                            onChange={(e) => setQ(e.target.value)}
                            placeholder="영화 제목 / ID"
                        />
                    </div>

                    <div className="admin-movie-list__count">
                        총 <strong>{filtered.length}</strong>개
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
                                        아직 등록된 영화가 없습니다. “영화(초안) 등록”로 시작해보세요.
                                    </td>
                                </tr>
                            ) : (
                                filtered.map((m) => (
                                    <tr
                                        key={m.movieId}
                                        className="admin-movie-list__row"
                                        onClick={() => nav(`/admin/movies/${m.movieId}`)}
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
            </div>
        </div>
    );
};

export default AdminMovieListPage;