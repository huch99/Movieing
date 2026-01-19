import React, { useEffect, useMemo, useState } from 'react';
import './AdminScheduleListPage.css';
import { useNavigate, useParams, useSearchParams } from 'react-router-dom';
import { adminScheduleApi } from './adminScheduleApi';

const STATUS_LABEL = [
  { value : "ALL", label : "전체" },
  { value : "DRAFT", label : "임시 저장" },
  { value : "OPEN", label : "오픈"},
  { value : "CLOSED", label : "마감"},
  { value : "CANCELED", label : "최소"},
  { value : "DELETED", label : "삭제됨"},
];

const AdminScheduleListPage = () => {
  const navigate = useNavigate();
  const { theaterId } = useParams();
  const [searchParams] = useSearchParams();
  const theaterName = searchParams.get("theaterName");

  const [loading, setLoading] = useState(false);
  const [pageData, setPageData] = useState([]);

  const [q, setQ] = useState("");
  const [status, setStatus] = useState("ALL");

  const [page, setPage] = useState(0);
  const size = 10;

  const content = useMemo(() => pageData?.content ?? [], [pageData]);

  const filtered = useMemo(() => {
    const keyword = q.trim().toLowerCase();
    return (content || [])
      .filter((it) => {
        if(status === "ALL") return true;
        return String(it.status) === status;
      })
      .filter((it) => {
        if(!keyword) return true;
        const hay = [
          it.screenName,
          it.title,
          it.scheduledDate,
        ]
          .filter(Boolean)
          .join(" ")
          .toLowerCase();
        return hay.includes(keyword);
      })
  }, [content, q, status]);

  const load = async (nextPage = page, nextStatus = status) => {
    setLoading(true);
    try {
      const params = {
        page: nextPage,
        size,
        sort: "scheduleId,desc",
        theaterId: Number(theaterId),
        ...(nextStatus ? { status: nextStatus } : {}),
      };

      const data = await adminScheduleApi.getList(params);
      setPageData(data);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    setPage(0);
    load(0, status);
  }, [theaterId]);

  const goPrev = () => {
    const next = Math.max(0, page - 1);
    setPage(next);
    load(next);
  }

  const goNext = () => {
    const totalPages = pageData?.totalPages ?? 0;
    const next = Math.min(totalPages - 1, page + 1);
    setPage(next);
    load(next);
  };

  const goCreate = async () => {
    try {
      const scheduleId = await adminScheduleApi.createDraft({
        // 최소 정보만 (또는 빈 객체)
        movieId: null,
        scheduledDate: null,
        startAt: null,
      });

      navigate(`/admin/schedules/${scheduleId}/detail`);
    } catch (e) {
      console.error(e);
      alert("스케줄 초안 생성에 실패했습니다.");
    }
  };

  const goScheduleDetail = (scheduleId) => {
    navigate(`/admin/schedules/${scheduleId}/detail?theaterId=${theaterId}`);
  };

  return (
    <div className="admin-theater-list">
      {/* Header */}
      <div className="admin-theater-list__header">
        <div>
          <h2 className="admin-theater-list__title">{theaterName} 스케줄 관리</h2>
          <p className="admin-theater-list__desc">
            선택한 영화관의 상영 스케줄을 관리합니다.
          </p>
        </div>

        <div className="admin-theater-list__actions">
          <button onClick={() => load(page, status)} disabled={loading}>
            새로고침
          </button>
          <button onClick={goCreate} disabled={loading}>
            + 스케줄 등록
          </button>
        </div>
      </div>

      {/* Filters */}
      <div className="admin-theater-list__filters">
        <div className="admin-theater-list__filter">
          <label>상태</label>
          <select value={status} onChange={(e) => setStatus(e.target.value)}>
            {STATUS_LABEL.map((o) => (
              <option key={o.value} value={o.value}>
                {o.label}
              </option>
            ))}
          </select>
        </div>

        <div className="admin-theater-list__filter admin-theater-list__filter--grow">
          <label>검색</label>
          <input
            value={q}
            onChange={(e) => setQ(e.target.value)}
            placeholder="영화명 / 상영관"
          />
        </div>

        <div className="admin-theater-list__count">
          총 <strong>{pageData?.totalElements ?? 0}</strong>개
        </div>
      </div>

      {/* Table */}
      <div className="admin-theater-list__table-wrap">
        <table className="admin-theater-list__table">
          <thead>
            <tr>
              <th>상영관</th>
              <th>영화</th>
              <th>날짜</th>
              <th>시간</th>
              <th>상태</th>
            </tr>
          </thead>

          <tbody>
            {loading && (
              <tr>
                <td colSpan={5} className="admin-theater-list__empty">
                  로딩 중...
                </td>
              </tr>
            )}

            {!loading && content.length === 0 && (
              <tr>
                <td colSpan={5} className="admin-theater-list__empty">
                  데이터가 없습니다.
                </td>
              </tr>
            )}

            {!loading &&
              filtered.map((s) => (
                <tr
                  key={s.scheduleId}
                  onClick={() => goScheduleDetail(s.scheduleId)}
                  style={{ cursor: "pointer" }}
                >
                  <td>{s.screenName}</td>
                  <td>
                    <strong>{s.title ?? "-"}</strong>
                    <div style={{ fontSize: 12, opacity: 0.7 }}>
                      {s.runtimeMin ? `${s.runtimeMin}분` : ""}
                    </div>
                  </td>
                  <td>{s.scheduledDate ?? "-"}</td>
                  <td>
                    {s.startAt ?? "-"}
                    {s.endAt ? ` ~ ${s.endAt}` : ""}
                  </td>
                  <td>
                    <span className={`status-badge status-${s.status}`}>
                      {STATUS_LABEL[s.status] ?? s.status}
                    </span>
                  </td>
                </tr>
              ))}
          </tbody>
        </table>
      </div>

      {/* Pager */}
      <div className="pager">
        <button onClick={goPrev} disabled={loading || page <= 0}>
          이전
        </button>
        <div className="pager__info">
          {pageData ? `${page + 1} / ${pageData.totalPages}` : "-"}
        </div>
        <button
          onClick={goNext}
          disabled={loading || !pageData || page + 1 >= pageData.totalPages}
        >
          다음
        </button>
      </div>
    </div>
  );
};

export default AdminScheduleListPage;