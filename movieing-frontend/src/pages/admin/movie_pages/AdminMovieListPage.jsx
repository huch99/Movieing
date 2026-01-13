import React, { useEffect, useState } from 'react';
import './AdminMovieListPage.css';
import { useNavigate } from 'react-router-dom';
import { adminMovieApi } from './adminMovieApi';

const AdminMovieListPage = () => {
    const nav = useNavigate();
    const [items, setItems] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

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
                                items.map((m) => (
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