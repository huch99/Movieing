import React, { useEffect, useMemo, useState } from 'react';
import './AdminTheaterList_SchedulePage.css';
import { useNavigate } from 'react-router-dom';
import { adminScheduleApi } from './adminScheduleApi';

const STATUS_OPTIONS = [
  { value: "ALL", label: "전체" },
  { value: "DRAFT", label: "임시 저장"},
  { value: "ACTIVE", label: "활성화" },
  { value: "HIDDEN", label: "숨김" },
  { value : "CLOSED", label: "운영 종료"},
  { value: "DELETED", label: "삭제됨"},
];

const AdminTheaterList_SchedulePage = () => {
  const navigate = useNavigate();

  const [loading, setLoading] = useState(false);
  const [items, setItems] = useState([]);

  const [q, setQ] = useState("");
  const [status, setStatus] = useState("ALL");

  const filtered = useMemo(() => {
    const keyword = q.trim().toLowerCase();
    return (items || [])
      .filter((it) => {
        if (status === "ALL") return true;
        return String(it.status) === status;
      })
      .filter((it) => {
        if (!keyword) return true;
        const hay = [it.theaterId, it.name].filter(Boolean).join(" ").toLowerCase();
        return hay.includes(keyword);
      });
  }, [items, q, status]);

  const load = async () => {
    setLoading(true);
    try {
      const data = await adminScheduleApi.getTheaters();
      setItems(Array.isArray(data) ? data : []);
    } catch (e) {
      console.error(e);
      alert("영화관 목록 조회에 실패했습니다.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load()
  }, []);

  const goSchedules = (theaterId, theaterName) => {
    navigate(`/admin/schedules/${theaterId}?theaterName=${encodeURIComponent(theaterName)}`);
  };

  return (
    <div className="admin-theater-list">
      {/* 헤더 */}
      <div className="admin-theater-list__header">
        <div>
          <h2 className="admin-theater-list__title">스케줄 등록 - 영화관 선택</h2>
          <p className="admin-theater-list__desc">
            영화관을 선택하면 해당 영화관의 스케줄 관리 페이지로 이동합니다.
          </p>
        </div>

        <div className="admin-theater-list__actions">
          <button onClick={load} disabled={loading}>
            새로고침
          </button>
        </div>
      </div>

      {/* 필터 */}
      <div className="admin-theater-list__filters">
        <div className="admin-theater-list__filter">
          <label>상태</label>
          <select value={status} onChange={(e) => setStatus(e.target.value)}>
            {STATUS_OPTIONS.map((o) => (
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
            placeholder="이름 / ID"
          />
        </div>

        <div className="admin-theater-list__count">
          총 <strong>{filtered.length}</strong>개
        </div>
      </div>

      {/* 테이블 */}
      <div className="admin-theater-list__table-wrap">
        <table className="admin-theater-list__table">
          <thead>
            <tr>
              <th>ID</th>
              <th>영화관명</th>
              <th>상태</th>
              <th>작업</th>
            </tr>
          </thead>

          <tbody>
            {loading && (
              <tr>
                <td colSpan={4} className="admin-theater-list__empty">
                  로딩 중...
                </td>
              </tr>
            )}

            {!loading && filtered.length === 0 && (
              <tr>
                <td colSpan={4} className="admin-theater-list__empty">
                  표시할 영화관이 없습니다.
                </td>
              </tr>
            )}

            {!loading &&
              filtered.map((it) => {
                const id = it.theaterId ?? it.id;
                const name = it.theaterName ?? it.name;
                return (
                  <tr key={id}>
                    <td>{id}</td>
                    <td>{it.theaterName}</td>
                    <td>
                      <span className={`status-badge status-${it.status}`}>
                        {it.status}
                      </span>
                    </td>
                    <td>
                      <button
                        type="button"
                        className="admin-theater-list__action"
                        onClick={() => goSchedules(id, name)}
                      >
                        선택
                      </button>
                    </td>
                  </tr>
                );
              })}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default AdminTheaterList_SchedulePage;