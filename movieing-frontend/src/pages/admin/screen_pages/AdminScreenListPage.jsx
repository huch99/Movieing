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

const fmtDateTime = (v) => {
  if (!v) return "-";
  const d = new Date(v);
  return Number.isNaN(d.getTime()) ? String(v) : d.toLocaleString();
};

const AdminScreenListPage = ({ theaterId }) => {
  const navigate = useNavigate();

  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(null); // Page<ScreenListItem>
  const [status, setStatus] = useState("ALL");
  const [q, setQ] = useState("");

  const load = async (opts = {}) => {
    if (!theaterId) return;

    setLoading(true);
    try {
      const p = await adminScreenApi.getListByTheater(theaterId, {
        page: opts.page ?? page?.number ?? 0,
        size: opts.size ?? page?.size ?? 20,
        sort: "updatedAt,desc",
      });
      setPage(p);
    } catch (e) {
      console.error(e);
      alert("상영관 목록 조회에 실패했습니다.");
    } finally {
      setLoading(false);
    }
  };

  const filtered = useMemo(() => {
    const arr = page?.content ?? [];
    const keyword = q.trim().toLowerCase();

    return arr
      .filter((s) => (status === "ALL" ? true : String(s.status) === status))
      .filter((s) => {
        if (!keyword) return true;
        const hay = [
          s.screenId,
          s.screenName,
          s.seatRowCount,
          s.seatColumnCount,
        ]
          .filter(Boolean)
          .join(" ")
          .toLowerCase();
        return hay.includes(keyword);
      });
  }, [page, status, q]);

  useEffect(() => {
    if (theaterId) load({ page: 0 });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [theaterId]);

  const onCreateDraft = async () => {
    const ok = window.confirm("상영관 초안을 생성할까요?");
    if (!ok) return;

    try {
      const screenId = await adminScreenApi.createDraftByTheater(theaterId);
      if (!screenId) {
        alert("초안은 생성됐지만 ID를 확인할 수 없습니다.");
        load({ page: 0 });
        return;
      }
      navigate(`/admin/screens/${screenId}`);
    } catch (e) {
      console.error(e);
      alert("상영관 초안 생성에 실패했습니다.");
    }
  };

  const canPrev = page && page.number > 0;
  const canNext = page && page.number < (page.totalPages ?? 1) - 1;

  return (
    <section className="admin-screen-list">
      {/* ===== Header ===== */}
      <div className="admin-screen-list__top">
        <h3 className="admin-screen-list__title">상영관</h3>

        <div className="admin-screen-list__actions">
          <button onClick={() => load({ page: 0 })} disabled={loading}>
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
          <input
            value={q}
            onChange={(e) => setQ(e.target.value)}
            placeholder="상영관명 / 좌석 / ID"
          />
        </div>

        <div className="admin-screen-list__count">
          총 <strong>{page?.totalElements ?? 0}</strong>개
        </div>
      </div>

      {/* ===== Table ===== */}
      <div className="admin-screen-list__table-wrap">
        <table className="admin-screen-list__table">
          <thead>
            <tr>
              <th>ID</th>
              <th>상영관명</th>
              <th>좌석</th>
              <th>상태</th>
              <th>수정일</th>
              <th>작업</th>
            </tr>
          </thead>
          <tbody>
            {loading && (
              <tr>
                <td colSpan={6} className="admin-screen-list__empty">
                  로딩 중...
                </td>
              </tr>
            )}

            {!loading && filtered.length === 0 && (
              <tr>
                <td colSpan={6} className="admin-screen-list__empty">
                  표시할 상영관이 없습니다.
                </td>
              </tr>
            )}

            {!loading &&
              filtered.map((s) => {
                const id = s.screenId ?? s.id;
                return (
                  <tr key={id}>
                    <td>{id}</td>
                    <td>{s.screenName ?? "-"}</td>
                    <td>
                      {s.seatRowCount ?? "-"} x {s.seatColumnCount ?? "-"}
                    </td>
                    <td>
                      <span className={`status-badge status-${s.status}`}>
                        {s.status}
                      </span>
                    </td>
                    <td>{fmtDateTime(s.updatedAt)}</td>
                    <td>
                      <button
                        className="admin-screen-list__link"
                        onClick={() => navigate(`/admin/screens/${id}`)}
                      >
                        상세
                      </button>
                    </td>
                  </tr>
                );
              })}
          </tbody>
        </table>

        {/* ===== Pager ===== */}
        {page && (
          <div className="admin-screen-list__pager">
            <button
              disabled={!canPrev || loading}
              onClick={() => load({ page: page.number - 1 })}
            >
              이전
            </button>
            <span>
              {page.number + 1} / {page.totalPages || 1}
            </span>
            <button
              disabled={!canNext || loading}
              onClick={() => load({ page: page.number + 1 })}
            >
              다음
            </button>
          </div>
        )}
      </div>
    </section>
  );
};

export default AdminScreenListPage;
