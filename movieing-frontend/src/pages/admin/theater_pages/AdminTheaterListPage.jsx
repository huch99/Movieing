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
  // ISO 문자열 가정
  const d = new Date(v);
  if (Number.isNaN(d.getTime())) return String(v);
  return d.toLocaleString();
};

const AdminTheaterListPage = () => {
  const navigate = useNavigate();

  const [loading, setLoading] = useState(false);
  const [items, setItems] = useState([]);

  // UI 상태
  const [q, setQ] = useState("");
  const [status, setStatus] = useState("ALL");

  // 서버에서 이미 필터링을 지원하면 여기서 params로 보내도록 바꿔도 됨
  const filtered = useMemo(() => {
    const keyword = q.trim().toLowerCase();
    return (items || [])
      .filter((it) => {
        if (status === "ALL") return true;
        return String(it.status) === status;
      })
      .filter((it) => {
        if (!keyword) return true;
        const hay = [
          it.theaterId,
          it.name,
          it.theaterName, // 혹시 필드명이 다를 수 있어서 대비
          it.address,
          it.city,
        ]
          .filter(Boolean)
          .join(" ")
          .toLowerCase();
        return hay.includes(keyword);
      });
  }, [items, q, status]);

  const load = async () => {
    setLoading(true);
    try {
      const arr = await adminTheaterApi.getList(); // ✅ List 그대로
      setItems(Array.isArray(arr) ? arr : []);
    } catch (e) {
      console.error(e);
      alert("영화관 목록 조회에 실패했습니다.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

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
            placeholder="이름 / 주소 / ID"
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
              <th>이름</th>
              <th>주소</th>
              <th>상태</th>
              <th>수정일</th>
              <th>작업</th>
            </tr>
          </thead>

          <tbody>
            {loading && (
              <tr>
                <td colSpan={6} className="admin-theater-list__empty">
                  로딩 중...
                </td>
              </tr>
            )}

            {!loading && filtered.length === 0 && (
              <tr>
                <td colSpan={6} className="admin-theater-list__empty">
                  표시할 영화관이 없습니다.
                </td>
              </tr>
            )}

            {!loading &&
              filtered.map((it) => {
                const id = it.theaterId ?? it.id;
                return (
                  <tr key={id}>
                    <td>{id}</td>
                    <td>
                      <Link
                        to={`/admin/theaters/${id}`}
                        className="admin-theater-list__link"
                      >
                        {it.name ?? it.theaterName}
                      </Link>
                    </td>
                    <td>{it.address ?? "-"}</td>
                    <td>
                      <span className={`status-badge status-${it.status}`}>
                        {it.status}
                      </span>
                    </td>
                    <td>{fmtDateTime(it.updatedAt)}</td>
                    <td>
                      <Link
                        to={`/admin/theaters/${id}`}
                        className="admin-theater-list__action"
                      >
                        상세
                      </Link>
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

export default AdminTheaterListPage;
