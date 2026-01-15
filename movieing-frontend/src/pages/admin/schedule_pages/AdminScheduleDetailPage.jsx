import React, { useEffect, useMemo, useState } from 'react';
import './AdminScheduleDetailPage.css';
import { useNavigate, useParams } from 'react-router-dom';
import { adminScheduleApi } from './adminScheduleApi';

const ALLOWED_MOVIE_STATUS = new Set(["COMMING_SOON", "NOW_SHOWING"]);

const AdminScheduleDetailPage = () => {
    const navigate = useNavigate();
    const { scheduleId } = useParams();

    const [loading, setLoading] = useState();
    const [saving, setSaving] = useState(false);
    const [data, setData] = useState(null);

    const [moviesLoading, setMoviesLoading] = useState(false);
    const [movies, setMovies] = useState([]);

    const [form, setForm] = useState({
        movieId: "",
        scheduledDate: "",
        startAt: "",
    });

    const movieOptions = useMemo(() => {
        return (movies || [])
            .filter((m) => ALLOWED_MOVIE_STATUS.has(String(m.status)))
            .map((m) => ({
                movieId: m.movieId,
                title: m.title ?? `#${m.movieId}`,
                status: m.status,
            }));
    }, [movies]);

    const loadMovies = async () => {
        setMoviesLoading(true);
        try {
            const arr = await adminScheduleApi.getMovies({
                statuses: "COMMING_SOON,NOW_SHOWING",
            });
            setMovies(arr);
        } catch (e) {
            console.error(e);
            alert("영화 목록 조회에 실패했습니다.");
        } finally {
            setMoviesLoading(false);
        }
    };

    const load = async () => {
        setLoading(true);
        try {
            const d = await adminScheduleApi.getDetail(scheduleId);
            setData(d);
            setForm({
                movieId: d?.movieId ?? "",
                scheduledDate: d?.scheduledDate ?? "",
                startAt: d?.startAt ?? "",
            });
        } catch (e) {
            console.error(e);
            alert("스케줄 상세 조회에 실패 했습니다.");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        load();
        loadMovies();
    }, [scheduleId]);

    const onChange = (k) => (e) => {
        setForm((prev) => ({ ...prev, [k]: e.target.value }));
    };

    // ✅ 상태 기반 버튼 활성화
    const status = data?.status ?? "DRAFT";
    const isDraft = status === "DRAFT";
    const isOpen = status === "OPEN";

    // ✅ 공통 payload
    const buildBody = () => ({
        movieId: Number(form.movieId),
        scheduledDate: form.scheduledDate,
        startAt: form.startAt,
    });

    // ✅ 임시 저장(DRAFT일 때만)
    const onSaveDraft = async () => {
        try {
            setSaving(true);

            // 1) 백엔드에 saveDraft가 있으면 이거 사용
            await adminScheduleApi.saveDraft(scheduleId, buildBody());

            alert("임시 저장 완료");
            await load();
        } catch (e) {
            console.error(e);
            alert("임시 저장에 실패했습니다.");
        } finally {
            setSaving(false);
        }
    };

    // ✅ 저장(OPEN일 때만) - 요청대로
    const onUpdate = async () => {
        try {
            setSaving(true);
            await adminScheduleApi.update(scheduleId, buildBody());
            alert("저장 완료");
            await load();
        } catch (e) {
            console.error(e);
            alert("수정에 실패 했습니다.");
        } finally {
            setSaving(false);
        }
    };

    // ✅ 완료(OPEN) - 일반적으로 DRAFT에서만 활성화하는 게 자연스러움
    const onComplete = async () => {
        try {
            setSaving(true);
            await adminScheduleApi.complete(scheduleId, buildBody());
            alert("완료(OPEN) 처리 완료");
            await load();
        } catch (e) {
            console.error(e);
            alert("완료 처리에 실패했습니다.");
        } finally {
            setSaving(false);
        }
    };

    const onCancel = async () => {
        if (!window.confirm("취소 처리할까요?")) return;
        try {
            setSaving(true);
            await adminScheduleApi.cancel(scheduleId);
            await load();
        } catch (e) {
            console.error(e);
            alert("취소에 실패했습니다.");
        } finally {
            setSaving(false);
        }
    };

    const onDelete = async () => {
        if (!window.confirm("삭제(소프트 삭제)할까요?")) return;
        try {
            setSaving(true);
            await adminScheduleApi.remove(scheduleId);
            alert("삭제 완료");
            navigate(-1);
        } catch (e) {
            console.error(e);
            alert("삭제에 실패했습니다.");
        } finally {
            setSaving(false);
        }
    };

    return (
        <div className="admin-theater-list">
            <div className="admin-theater-list__header">
                <div>
                    <h2 className="admin-theater-list__title">스케줄 상세</h2>
                    <p className="admin-theater-list__desc">
                        ID {scheduleId} · 상태{" "}
                        <span className={`status-badge status-${data?.status ?? "DRAFT"}`}>
                            {data?.status ?? "-"}
                        </span>
                    </p>
                </div>

                <div className="admin-theater-list__actions">
                    <button onClick={() => navigate(-1)} disabled={loading || saving}>
                        뒤로
                    </button>
                    <button onClick={onCancel} disabled={loading || saving}>
                        취소
                    </button>
                    <button onClick={onDelete} disabled={loading || saving}>
                        삭제
                    </button>
                </div>
            </div>

            <div className="admin-theater-list__table-wrap">
                {(loading || moviesLoading) && (
                    <div className="admin-theater-list__empty">로딩 중...</div>
                )}

                {!loading && !data && (
                    <div className="admin-theater-list__empty">데이터가 없습니다.</div>
                )}

                {!loading && data && (
                    <div className="schedule-detail">
                        <div className="schedule-detail__grid">
                            <div className="field">
                                <label>영화 선택</label>
                                <select value={form.movieId} onChange={onChange("movieId")}>
                                    <option value="">선택</option>
                                    {movieOptions.map((m) => (
                                        <option key={m.movieId} value={m.movieId}>
                                            {m.title}
                                        </option>
                                    ))}
                                </select>
                            </div>

                            <div className="field">
                                <label>상영 날짜</label>
                                <input
                                    value={form.scheduledDate}
                                    onChange={onChange("scheduledDate")}
                                    placeholder="YYYY-MM-DD"
                                />
                            </div>

                            <div className="field">
                                <label>상영 시작 시간</label>
                                <input
                                    value={form.startAt}
                                    onChange={onChange("startAt")}
                                    placeholder="HH:mm:ss"
                                />
                            </div>

                            <div className="field">
                                <label>상영 종료 시간 (auto)</label>
                                <input value={data.endAt ?? ""} disabled />
                            </div>

                            <div className="field">
                                <label>영화 제목 (auto)</label>
                                <input value={data.title ?? ""} disabled />
                            </div>

                            <div className="field">
                                <label>상영 시간(분) (auto)</label>
                                <input value={data.runtimeMin ?? ""} disabled />
                            </div>
                        </div>

                        <div className="schedule-detail__actions">
                            {/* ✅ 임시 저장: DRAFT에서만 */}
                            <button
                                onClick={onSaveDraft}
                                disabled={saving || !isDraft}
                                className="primary"
                                type="button"
                            >
                                임시 저장
                            </button>

                            {/* ✅ 저장: OPEN에서만 (요청사항) */}
                            <button
                                onClick={onUpdate}
                                disabled={saving || !isOpen}
                                className="primary"
                                type="button"
                            >
                                저장
                            </button>

                            {/* ✅ 완료(OPEN): 보통 DRAFT에서만 */}
                            <button
                                onClick={onComplete}
                                disabled={saving || !isDraft}
                                className="primary"
                                type="button"
                            >
                                완료(OPEN)
                            </button>
                        </div>

                        {/* 선택: 힌트 문구 */}
                        {!isDraft && (
                            <div className="admin-theater-list__empty" style={{ marginTop: 10 }}>
                                * 임시 저장/완료는 DRAFT 상태에서만 가능합니다.
                                {isOpen && " (저장은 OPEN에서만 가능)"}
                            </div>
                        )}
                    </div>
                )}
            </div>
        </div>
    );
};

export default AdminScheduleDetailPage;