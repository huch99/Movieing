import React, { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { adminScreenApi } from "./adminScreenApi";
import './AdminScreenListPage.css';

const STATUS_OPTIONS = [
  { value: "ALL", label: "전체" },
  { value: "DRAFT", label: "DRAFT" },
  { value: "ACTIVE", label: "ACTIVE" },
  { value: "HIDDEN", label: "HIDDEN" },
  { value: "CLOSED", label: "CLOSED" },
  { value: "DELETED", label: "DELETED" },
];

const AdminScreenListPage = ({ theaterId }) => {
  const navigate = useNavigate();

  const [screens, setScreens] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const [page, setPage] = useState(0);
  const [totalPage, setTotalPage] = useState(0);

  const [status, setStatus] = useState("ALL");
  const [keywords, setKeywords] = useState("");
  const [currentKeywords, setCurrentKeywords] = useState("");



  const load = async (nextPage = page) => {
    if (!theaterId) return;
    setLoading(true);
    setError(null);

    try {
      const params = {
        page: nextPage,
        size: 20,
        sort: "createdAt,desc",
        ...(status && status !== "ALL" ? { status } : {}),
        ...(keywords?.trim() ? { keywords: keywords.trim() } : {})
      }
      const data = await adminScreenApi.getListByTheater(theaterId, params);
      setScreens(data?.content ? data.content : []);
      setTotalPage(data.totalPages);
    } catch (e) {
      console.error(e);
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    setPage(0);
    load();
  }, [status, keywords, theaterId]);

  const onCreateDraft = async () => {
    const ok = window.confirm("상영관 초안을 생성할까요?");
    if (!ok) return;

    try {
      const screenId = await adminScreenApi.createDraftByTheater(theaterId);
      if (!screenId) {
        alert("초안은 생성됐지만 ID를 확인할 수 없습니다.");
        load();
        return;
      }
      navigate(`/admin/screens/${screenId}`);
    } catch (e) {
      console.error(e);
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

  return (
    <section className="admin-screen-list">
      {/* ===== Header ===== */}
      <div className="admin-screen-list__top">
        <h3 className="admin-screen-list__title">상영관</h3>

        <div className="admin-screen-list__actions">
          <button onClick={() => load()} disabled={loading}>
            새로고침
          </button>
          <button onClick={onCreateDraft} disabled={loading}>
            + 상영관 초안 생성
          </button>
        </div>
      </div>

      {/* ===== Filters ===== */}
      <div className="admin-screen-list__filters">
        <div>
          <label>상태</label>
          <select value={status} onChange={(e) => setStatus(e.target.value)}>
            {STATUS_OPTIONS.map((o) => (
              <option key={o.value} value={o.value}>
                {o.label}
              </option>
            ))}
          </select>
        </div>

        <div className="admin-screen-list__filter--grow">
          <label>검색</label>
          <form onSubmit={(e) => {
            e.preventDefault();
            setKeywords(currentKeywords.trim());
            setCurrentKeywords("");
          }}>
            <input
              value={currentKeywords}
              onChange={(e) => setCurrentKeywords(e.target.value)}
              placeholder="상영관명 / ID"
            />
          </form>
        </div>

        <div className="admin-screen-list__count">
          총 <strong>{screens?.length ?? 0}</strong>개
        </div>
      </div>

      {/* ===== Table ===== */}
      <div className="admin-screen-list__table-wrap">
        <table className="admin-screen-list__table">
          <thead>
            <tr>
              <th>ID</th>
              <th>상영관명</th>
              <th>수용인원</th>
              <th>상태</th>
              <th>작업</th>
            </tr>
          </thead>
          <tbody>
            {error ? (
              <tr>
                <td colSpan={5} className="admin-screen-list__empty">
                  에러 : {String(error)}
                </td>
              </tr>
            ) : loading ? (
              <tr>
                <td colSpan={5} className="admin-screen-list__empty">
                  로딩 중...
                </td>
              </tr>
            ) : screens.length === 0 ? (
              <tr>
                <td colSpan={5} className="admin-screen-list__empty">
                  표시할 상영관이 없습니다.
                </td>
              </tr>
            ) : (
              screens.map((s) => (
                <tr key={s.screenId}>
                  <td>{s.screenId}</td>
                  <td>{s.screenName ?? "-"}</td>
                  <td>{s.capacity} 명</td>
                  <td>
                    <span className={`status-badge status-${s.status}`}>{s.status}</span>
                  </td>
                  <td>
                    <button
                      type="button"
                      className="admin-screen-list__link"
                      onClick={() => navigate(`/admin/screens/${s.screenId}`)}
                    >
                      상세
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>

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
    </section>
  );
};

export default AdminScreenListPage;
