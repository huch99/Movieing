import React, { useEffect, useMemo, useState } from 'react';
import './AdminTheaterList_SchedulePage.css';
import { useNavigate } from 'react-router-dom';
import { adminScheduleApi } from './adminScheduleApi';

const STATUS_OPTIONS = [
  { value: "ALL", label: "전체" },
  { value: "DRAFT", label: "임시 저장" },
  { value: "ACTIVE", label: "활성화" },
  { value: "HIDDEN", label: "숨김" },
  { value: "CLOSED", label: "운영 종료" },
  { value: "DELETED", label: "삭제됨" },
];

const AdminTheaterList_SchedulePage = () => {
  const navigate = useNavigate();

  const [page, setPage] = useState(0);
  const [totalPage, setTotalPage] = useState(0);

  const [loading, setLoading] = useState(false);
  const [items, setItems] = useState([]);

  const [keywords, setKeywords] = useState("");
  const [currentKeywords, setCurrentKeywords] = useState("");
  const [status, setStatus] = useState("ALL");

  const load = async (nextPage = page) => {
    setLoading(true);
    try {
      const params = {
        page: nextPage,
        size: 20,
        sort: "createdAt,desc",
        ...(status && status !== "ALL" ? {status} : {status}),
        ...(keywords?.trim() ? {keywords: keywords.trim()} : {})
      }
      const data = await adminScheduleApi.getTheaters(params);
      setItems(data?.content ? data.content : []);
      setTotalPage(data.totalPages);
    } catch (e) {
      console.error(e);
      alert("영화관 목록 조회에 실패했습니다.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    setPage(0);
    load();
  }, [status, keywords]);

  const goSchedules = (theaterId, theaterName) => {
    navigate(`/admin/schedules/${theaterId}?theaterName=${encodeURIComponent(theaterName)}`);
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
          <form onSubmit={(e) => {
            e.preventDefault();
            setKeywords(currentKeywords.trim());
            setCurrentKeywords("");
          }}>
            <input
              value={currentKeywords}
              onChange={(e) => setCurrentKeywords(e.target.value)}
              placeholder="이름 / ID"
            />
          </form>
        </div>

        <div className="admin-theater-list__count">
          총 <strong>{items.length}</strong>개
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

            {!loading && items.length === 0 && (
              <tr>
                <td colSpan={4} className="admin-theater-list__empty">
                  표시할 영화관이 없습니다.
                </td>
              </tr>
            )}

            {!loading &&
              items.map((i) => {
                return (
                  <tr key={i.theaterId}>
                    <td>{i.theaterId}</td>
                    <td>{i.theaterName}</td>
                    <td>
                      <span className={`status-badge status-${i.status}`}>
                        {i.status}
                      </span>
                    </td>
                    <td>
                      <button
                        type="button"
                        className="admin-theater-list__action"
                        onClick={() => goSchedules(i.theaterId, i.theaterName)}
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

      {/* ===== Pager ===== */}
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
  );
};

export default AdminTheaterList_SchedulePage;