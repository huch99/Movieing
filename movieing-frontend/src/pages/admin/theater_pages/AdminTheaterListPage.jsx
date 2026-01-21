import React, { useEffect, useMemo, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import "./AdminTheaterListPage.css";
import { adminTheaterApi } from "./adminTheaterApi";

const STATUS_OPTIONS = [
  { value: "ALL", label: "전체" },
  { value: "DRAFT", label: "임시 저장" },
  { value: "ACTIVE", label: "활성화" },
  { value: "HIDDEN", label: "숨김" },
  { value: "CLOSED", label: "운영 종료" },
  { value: "DELETED", label: "삭제됨" },
];

const fmtDateTime = (v) => {
  if (!v) return "-";
  const d = new Date(v);
  if (Number.isNaN(d.getTime())) return String(v);
  return d.toLocaleString();
};

const AdminTheaterListPage = () => {
  const navigate = useNavigate();

  const [page, setPage] = useState(0);
  const [totalPage, setTotalPage] = useState(0);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [items, setItems] = useState([]);

  const [stats, setStats] = useState({
    totalTheaters: 0,
    activeTheaters: 0,
    totalScreens: 0,
    activeScreens: 0,
    totalSeats: 0,
    activeSeats: 0
  });

  // UI 상태
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
        ...(status && status !== "ALL" ? { status } : {}),
        ...(keywords?.trim() ? { keywords: keywords.trim() } : {})
      }
      const arr = await adminTheaterApi.getList(params);
      setItems(arr?.content ?? []);
      setTotalPage(arr.totalPages);
    } catch (e) {
      console.error(e);
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  const statsLoad = async () => {
    try {
      const data = await adminTheaterApi.getStats();
      setStats(data);
    } catch (e) {
      console.error(e);
    }
  }

  useEffect(() => {
    statsLoad();
  }, []);

  useEffect(() => {
    load();
  }, [status, keywords])

  const onCreateDraft = async () => {
    try {
      const theaterId = await adminTheaterApi.createDraft(); // ✅ Long 그대로 리턴됨
      if (!theaterId) {
        alert("초안은 생성됐지만 ID를 확인할 수 없습니다. 목록을 새로고침합니다.");
        await load();
        return;
      }

      navigate(`/admin/theaters/${theaterId}`);
    } catch (e) {
      console.error(e);
      alert("영화관 초안 생성에 실패했습니다.");
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
        const next = Math.min(totalPages - 1, page + 1);
        setPage(next);
        load(next);
    };

  return (
    <div className="admin-theater-list">
      {/* 헤더 */}
      <div className="admin-theater-list__header">
        <div>
          <h2 className="admin-theater-list__title">영화관 관리</h2>
          <p className="admin-theater-list__desc">
            영화관을 생성·수정하고 상태를 관리합니다.
          </p>
        </div>

        <div className="admin-theater-list__actions">
          <button onClick={load} disabled={loading}>
            새로고침
          </button>
          <button onClick={onCreateDraft} disabled={loading}>
            + 영화관 초안 생성
          </button>
        </div>
      </div>

      <div className="admin-theater-list__stats">
        <div className="admin-theater-list__stats-table-wrap">
          <table className="admin-theater-list__stats-table">
            <thead className="admin-theater-list__stats-thead">
              <tr>
                <th>전체 영화관</th>
                <th>운영 영화관</th>
                <th>전체 상영관</th>
                <th>운영 상영관</th>
                <th>전체 좌석</th>
                <th>운영 좌석</th>
              </tr>
            </thead>

            <tbody className="admin-theater-list__stats-tbody">
              <tr>
                <td>{stats.totalTheaters}</td>
                <td>{stats.activeTheaters}</td>
                <td>{stats.totalScreens}</td>
                <td>{stats.activeScreens}</td>
                <td>{stats.totalSeats}</td>
                <td>{stats.activeSeats}</td>
              </tr>
            </tbody>
          </table>
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
              placeholder="이름 / 주소 / ID"
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
              <th>이름</th>
              <th>주소</th>
              <th>상태</th>
              <th>작업</th>
            </tr>
          </thead>

          <tbody>
            {error ? (
              <tr>
                <td className="admin-theater-list__empty" colSpan={6}>에러: {error}</td>
              </tr>
            ) : (
              loading ? (
                <tr>
                  <td colSpan={6} className="admin-theater-list__empty">
                    로딩 중...
                  </td>
                </tr>
              ) : (
                items.length === 0 ? (
                  <tr>
                    <td colSpan={6} className="admin-theater-list__empty">
                      표시할 영화관이 없습니다.
                    </td>
                  </tr>
                ) : (
                  items.map((i) => {
                    return (
                      <tr key={i.theaterId}>
                        <td>{i.theaterId}</td>
                        <td>
                          <Link
                            to={`/admin/theaters/${i.theaterId}`}
                            className="admin-theater-list__link"
                          >
                            {i.theaterName}
                          </Link>
                        </td>
                        <td>{i.address ?? "-"}</td>
                        <td>
                          <span className={`status-badge status-${i.status}`}>
                            {i.status}
                          </span>
                        </td>
                        <td>
                          <Link
                            to={`/admin/theaters/${i.theaterId}`}
                            className="admin-theater-list__action"
                          >
                            상세
                          </Link>
                        </td>
                      </tr>
                    )
                  })
                )
              )
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
  );
};

export default AdminTheaterListPage;
