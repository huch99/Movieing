import React, { useEffect, useMemo, useState } from "react";
import "./AdminSeatLayoutPage.css";
import { adminSeatApi } from "./adminSeatApi";

const STATUS_ORDER = ["ACTIVE", "INACTIVE", "BROKEN", "BLOCKED"];

// ✅ “원래 좌석 col” 기준으로, 이 col 뒤에 통로를 몇 칸 넣을지
// 예: 1 2 (통로 2칸) 3 4 5 6 7 8 (통로 2칸) 9 10
const AISLE_AFTER_COL = { 2: 1, 8: 1 };

const nextStatus = (cur) => {
  const i = STATUS_ORDER.indexOf(cur);
  if (i < 0) return STATUS_ORDER[0];
  return STATUS_ORDER[(i + 1) % STATUS_ORDER.length];
};

const AdminSeatLayoutPage = ({ screenId }) => {
  const [loading, setLoading] = useState(false);
  const [working, setWorking] = useState(false);

  const [seats, setSeats] = useState([]);
  const [statusForCreate, setStatusForCreate] = useState("ACTIVE");

  // 좌석 선택(우측 패널)
  const [selectedId, setSelectedId] = useState(null);

  const selectedSeat = useMemo(() => {
    if (!selectedId) return null;
    return seats.find((s) => String(s.seatId) === String(selectedId)) ?? null;
  }, [seats, selectedId]);

  // rowLabel -> index(1-based) / index -> label
  const rowToIndex = (rowLabel) => {
    if (!rowLabel) return 0;
    const s = String(rowLabel).trim().toUpperCase();
    let n = 0;
    for (let i = 0; i < s.length; i++) {
      const code = s.charCodeAt(i);
      if (code < 65 || code > 90) continue;
      n = n * 26 + (code - 64);
    }
    return n;
  };

  const indexToRow = (n) => {
    if (!n || n <= 0) return "";
    let x = n;
    let out = "";
    while (x > 0) {
      x--;
      out = String.fromCharCode(65 + (x % 26)) + out;
      x = Math.floor(x / 26);
    }
    return out;
  };

  // row/col 최대치 계산(그리드 렌더링)
  const { maxRowIdx, maxCol } = useMemo(() => {
    let maxColTmp = 0;
    let maxRowTmp = 0;

    for (const it of seats) {
      maxColTmp = Math.max(maxColTmp, Number(it.seatCol || 0));
      maxRowTmp = Math.max(maxRowTmp, rowToIndex(it.seatRow));
    }

    return { maxRowIdx: maxRowTmp, maxCol: maxColTmp };
  }, [seats]);

  // 좌석 map: "rowIdx-col" -> seat
  const seatMap = useMemo(() => {
    const m = new Map();
    for (const s of seats) {
      const r = rowToIndex(s.seatRow);
      const c = Number(s.seatCol);
      if (!r || !c) continue;
      m.set(`${r}-${c}`, s);
    }
    return m;
  }, [seats]);

  const load = async () => {
    if (!screenId) return;
    setLoading(true);
    try {
      const list = await adminSeatApi.getLayout(screenId);
      const arr = Array.isArray(list) ? list : [];
      setSeats(arr);

      // 선택 좌석이 삭제됐을 수 있으니 정리
      setSelectedId((prev) => {
        if (!prev) return prev;
        const ok = arr.some((x) => String(x.seatId) === String(prev));
        return ok ? prev : null;
      });
    } catch (e) {
      console.error(e);
      alert(
        e?.response?.data?.resultMessage ??
          e?.message ??
          "좌석 배치도 조회 실패"
      );
      setSeats([]);
      setSelectedId(null);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
    // eslint-disable-next-line
  }, [screenId]);

  const hasSeats = seats.length > 0;

  const onGenerate = async () => {
    if (!screenId) return;
    const ok = window.confirm(
      "좌석을 생성할까요? (Screen의 row/col 기준으로 생성됩니다.)"
    );
    if (!ok) return;

    try {
      setWorking(true);
      await adminSeatApi.generateSeats(screenId, { status: statusForCreate });
      await load();
      alert("좌석이 생성되었습니다.");
    } catch (e) {
      console.error(e);
      alert(e?.response?.data?.resultMessage ?? e?.message ?? "좌석 생성 실패");
    } finally {
      setWorking(false);
    }
  };

  const onRegenerate = async () => {
    if (!screenId) return;
    const ok = window.confirm(
      "좌석을 재생성할까요?\n기존 좌석은 모두 삭제(물리)되고 다시 생성됩니다."
    );
    if (!ok) return;

    try {
      setWorking(true);
      await adminSeatApi.regenerateSeats(screenId, { status: statusForCreate });
      await load();
      alert("좌석이 재생성되었습니다.");
    } catch (e) {
      console.error(e);
      alert(
        e?.response?.data?.resultMessage ?? e?.message ?? "좌석 재생성 실패"
      );
    } finally {
      setWorking(false);
    }
  };

  const onClickSeat = async (seat) => {
    if (!seat) return;
    setSelectedId(seat.seatId);

    const ns = nextStatus(seat.status);

    try {
      setWorking(true);
      await adminSeatApi.updateSeat(seat.seatId, {
        status: ns,
        seatRow: seat.seatRow,
        seatCol: seat.seatCol,
      });
      setSeats((prev) =>
        prev.map((x) => (x.seatId === seat.seatId ? { ...x, status: ns } : x))
      );
    } catch (e) {
      console.error(e);
      alert(
        e?.response?.data?.resultMessage ?? e?.message ?? "좌석 상태 변경 실패"
      );
      await load();
    } finally {
      setWorking(false);
    }
  };

  const onDeleteSeat = async () => {
    if (!selectedSeat) return;
    const ok = window.confirm(
      `좌석 ${selectedSeat.seatRow}${selectedSeat.seatCol} 를 삭제할까요?\n(물리 삭제)`
    );
    if (!ok) return;

    try {
      setWorking(true);
      await adminSeatApi.deleteSeat(selectedSeat.seatId);
      setSelectedId(null);
      await load();
      alert("좌석이 삭제되었습니다.");
    } catch (e) {
      console.error(e);
      alert(e?.response?.data?.resultMessage ?? e?.message ?? "좌석 삭제 실패");
      await load();
    } finally {
      setWorking(false);
    }
  };

  const rows = maxRowIdx || 0;
  const cols = maxCol || 0;

  // ✅ “표시용” 컬럼 배열: 실제 좌석 col 사이에 통로 칸을 끼워 넣음
  const displayCols = useMemo(() => {
    const out = [];
    for (let c = 1; c <= cols; c++) {
      out.push({ kind: "seat", col: c });
      const gap = AISLE_AFTER_COL[c];
      if (gap && gap > 0) {
        for (let k = 0; k < gap; k++)
          out.push({ kind: "aisle", key: `a-${c}-${k}` });
      }
    }
    return out;
  }, [cols]);

  return (
    <div className="admin-seat-layout">
      <div className="admin-seat-layout__card">
        <div className="admin-seat-layout__top">
          <div className="admin-seat-layout__title-wrap">
            <h3 className="admin-seat-layout__title">좌석 배치도</h3>
            <p className="admin-seat-layout__subtitle">
              좌석을 클릭하면 상태가 변경됩니다. (ACTIVE → INACTIVE → BROKEN →
              BLOCKED)
            </p>
          </div>

          <div className="admin-seat-layout__controls">
            <div className="admin-seat-layout__select-wrap">
              <label className="admin-seat-layout__label">생성 기본 상태</label>
              <select
                className="admin-seat-layout__select"
                value={statusForCreate}
                onChange={(e) => setStatusForCreate(e.target.value)}
                disabled={working}
              >
                {STATUS_ORDER.map((s) => (
                  <option key={s} value={s}>
                    {s}
                  </option>
                ))}
              </select>
            </div>

            <div className="admin-seat-layout__btns">
              {!hasSeats ? (
                <button
                  className="admin-seat-layout__btn admin-seat-layout__btn--primary"
                  onClick={onGenerate}
                  disabled={working || loading}
                >
                  좌석 생성
                </button>
              ) : (
                <button
                  className="admin-seat-layout__btn admin-seat-layout__btn--danger"
                  onClick={onRegenerate}
                  disabled={working || loading}
                >
                  좌석 재생성
                </button>
              )}

              <button
                className="admin-seat-layout__btn admin-seat-layout__btn--ghost"
                onClick={load}
                disabled={working || loading}
              >
                새로고침
              </button>
            </div>
          </div>
        </div>

        <div className="admin-seat-layout__body">
          {/* 좌석 그리드 */}
          <div className="admin-seat-layout__grid-area">
            {loading ? (
              <div className="admin-seat-layout__empty">로딩 중...</div>
            ) : !hasSeats ? (
              <div className="admin-seat-layout__empty">
                아직 좌석이 없습니다. 상단의 <b>좌석 생성</b>으로 시작하세요.
              </div>
            ) : (
              <>
                <div className="admin-seat-layout__screen">SCREEN</div>

                <div
                  className="admin-seat-layout__grid"
                  style={{
                    gridTemplateColumns: `44px repeat(${displayCols.length}, 38px)`,
                  }}
                >
                  {/* 헤더(Col) */}
                  <div className="admin-seat-layout__corner" />
                  {displayCols.map((dc, i) => {
                    if (dc.kind === "aisle") {
                      return (
                        <div
                          key={dc.key ?? `aisle-head-${i}`}
                          className="admin-seat-layout__colhead admin-seat-layout__colhead--aisle"
                        />
                      );
                    }
                    return (
                      <div
                        key={`col-${dc.col}`}
                        className="admin-seat-layout__colhead"
                      >
                        {dc.col}
                      </div>
                    );
                  })}

                  {/* Rows */}
                  {Array.from({ length: rows }, (_, ri) => {
                    const rowIdx = ri + 1;
                    const rowLabel = indexToRow(rowIdx);

                    return (
                      <React.Fragment key={`row-${rowIdx}`}>
                        <div className="admin-seat-layout__rowhead">
                          {rowLabel}
                        </div>

                        {displayCols.map((dc, di) => {
                          // ✅ 통로 칸
                          if (dc.kind === "aisle") {
                            return (
                              <div
                                key={`aisle-${rowIdx}-${di}`}
                                className="admin-seat-layout__cell admin-seat-layout__cell--aisle"
                                title="통로"
                              />
                            );
                          }

                          const col = dc.col;
                          const seat = seatMap.get(`${rowIdx}-${col}`) || null;

                          // 빈 칸은 “없음(삭제됨)”으로 표시
                          if (!seat) {
                            return (
                              <div
                                key={`cell-${rowIdx}-${col}`}
                                className="admin-seat-layout__cell admin-seat-layout__cell--empty"
                                title="좌석 없음"
                              />
                            );
                          }

                          const isSelected =
                            selectedId &&
                            String(selectedId) === String(seat.seatId);

                          return (
                            <button
                              key={`seat-${seat.seatId}`}
                              type="button"
                              className={[
                                "admin-seat-layout__cell",
                                `admin-seat-layout__cell--${seat.status}`,
                                isSelected ? "is-selected" : "",
                              ]
                                .filter(Boolean)
                                .join(" ")}
                              onClick={() => onClickSeat(seat)}
                              disabled={working}
                              title={`${seat.seatRow}${seat.seatCol} · ${seat.status}`}
                            >
                              {seat.seatRow}
                              {seat.seatCol}
                            </button>
                          );
                        })}
                      </React.Fragment>
                    );
                  })}
                </div>

                <div className="admin-seat-layout__legend">
                  {STATUS_ORDER.map((s) => (
                    <span
                      key={s}
                      className={`admin-seat-layout__legend-item admin-seat-layout__legend-item--${s}`}
                    >
                      <span className="admin-seat-layout__legend-dot" />
                      {s}
                    </span>
                  ))}
                </div>
              </>
            )}
          </div>

          {/* 우측 패널 */}
          <div className="admin-seat-layout__side">
            <div className="admin-seat-layout__side-card">
              <div className="admin-seat-layout__side-title">선택 좌석</div>

              {!selectedSeat ? (
                <div className="admin-seat-layout__side-empty">
                  좌석을 클릭하면 상세 정보가 표시됩니다.
                </div>
              ) : (
                <>
                  <div className="admin-seat-layout__kv">
                    <div className="admin-seat-layout__k">좌석</div>
                    <div className="admin-seat-layout__v">
                      {selectedSeat.seatRow}
                      {selectedSeat.seatCol}
                    </div>
                  </div>

                  <div className="admin-seat-layout__kv">
                    <div className="admin-seat-layout__k">상태</div>
                    <div className="admin-seat-layout__v">
                      <span
                        className={`admin-seat-layout__pill admin-seat-layout__pill--${selectedSeat.status}`}
                      >
                        {selectedSeat.status}
                      </span>
                    </div>
                  </div>

                  <div className="admin-seat-layout__kv">
                    <div className="admin-seat-layout__k">ID</div>
                    <div className="admin-seat-layout__v">
                      #{selectedSeat.seatId}
                    </div>
                  </div>

                  <div className="admin-seat-layout__side-actions">
                    <button
                      className="admin-seat-layout__btn admin-seat-layout__btn--danger"
                      onClick={onDeleteSeat}
                      disabled={working}
                    >
                      좌석 삭제
                    </button>
                    <div className="admin-seat-layout__hint">
                      좌석 삭제는 <b>물리 삭제</b>입니다.
                    </div>
                  </div>
                </>
              )}
            </div>

            <div className="admin-seat-layout__side-card">
              <div className="admin-seat-layout__side-title">요약</div>
              <div className="admin-seat-layout__summary">
                <div className="admin-seat-layout__summary-item">
                  <span>총 좌석</span>
                  <b>{seats.length}</b>
                </div>
                <div className="admin-seat-layout__summary-item">
                  <span>그리드</span>
                  <b>
                    {rows} × {cols}
                  </b>
                </div>
              </div>
              <div className="admin-seat-layout__hint">
                삭제된 좌석은 그리드에서 빈 칸으로 표시됩니다.
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AdminSeatLayoutPage;
