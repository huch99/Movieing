import React, { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { adminTheaterApi } from "./adminTheaterApi";
import './AdminTheaterDetailPage.css';
import AdminScreenListPage from "../screen_pages/AdminScreenListPage";

const emptyForm = {
  name: "",
  address: "",
  city: "",
  phone: "",
  latitude: "",
  longitude: "",
  openTime: "",
  closeTime: "",
  description: "",
};

const toStr = (v) => (v === null || v === undefined ? "" : String(v));

const AdminTheaterDetailPage = () => {
  const navigate = useNavigate();
  const { theaterId } = useParams();

  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [entity, setEntity] = useState(null);
  const [form, setForm] = useState(emptyForm);

  const status = entity?.status || "DRAFT";

  const isDraft = status === "DRAFT";
  const isDeleted = status === "DELETED";
  const canEditNormal =
    status === "ACTIVE" || status === "HIDDEN" || status === "CLOSED";
  const canEditDraft = isDraft;

  const badgeClass = useMemo(() => {
    return `admin-theater-detail__badge admin-theater-detail__badge--${status}`;
  }, [status]);

  const load = async () => {
    if (!theaterId) return;
    setLoading(true);
    try {
      const data = await adminTheaterApi.getDetail(theaterId);
      setEntity(data);

      // 서버 DTO 필드명 차이를 최대한 흡수
      setForm({
        theaterName: toStr(data?.theaterName),
        address: toStr(data?.address),
        lat: toStr(data?.lat),
        lng: toStr(data?.lng),
        openTime: toStr(data?.openTime), // LocalTime -> "HH:mm:ss" 또는 "HH:mm" 로 올 수 있음
        closeTime: toStr(data?.closeTime),
      });
    } catch (e) {
      console.error(e);
      alert("영화관 상세 조회에 실패했습니다.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [theaterId]);

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [theaterId]);

  const onChange = (key) => (e) => {
    setForm((prev) => ({ ...prev, [key]: e.target.value }));
  };

  const numOrUndef = (v) => {
    const s = String(v ?? "").trim();
    if (s === "") return undefined;
    const n = Number(s);
    return Number.isFinite(n) ? n : undefined;
  };

  const trimOrUndef = (v) => {
    const s = String(v ?? "").trim();
    return s === "" ? undefined : s;
  };

  // 부분 수정/임시저장(Partial Update) 바디
  const buildPartialBody = () => ({
    theaterName: trimOrUndef(form.theaterName),
    address: trimOrUndef(form.address),
    lat: numOrUndef(form.lat),
    lng: numOrUndef(form.lng),
    openTime: trimOrUndef(form.openTime),
    closeTime: trimOrUndef(form.closeTime),
  });

  // 완료는 @Valid 가능성 높아서 trim 후 "필수는 값 보장" 형태로 전송
  const buildCompleteBody = () => ({
    theaterName: String(form.theaterName ?? "").trim(),
    address: String(form.address ?? "").trim(),
    lat: numOrUndef(form.lat), // 서버가 필수면 undefined면 validation 실패
    lng: numOrUndef(form.lng),
    openTime: String(form.openTime ?? "").trim() || null,
    closeTime: String(form.closeTime ?? "").trim() || null,
  });

  const onSaveDraft = async () => {
    setSaving(true);
    try {
      await adminTheaterApi.saveDraft(theaterId, buildPartialBody());
      alert("임시 저장 완료");
      await load();
    } catch (e) {
      console.error(e);
      alert("임시 저장에 실패했습니다.");
    } finally {
      setSaving(false);
    }
  };

  const onComplete = async () => {
    const ok = window.confirm(
      "완료 처리하면 상태가 ACTIVE로 전환됩니다. 진행할까요?"
    );
    if (!ok) return;

    setSaving(true);
    try {
      await adminTheaterApi.complete(theaterId, buildCompleteBody());
      alert("완료 처리 완료");
      await load();
    } catch (e) {
      console.error(e);
      alert("완료 처리에 실패했습니다. 필수값을 확인하세요.");
    } finally {
      setSaving(false);
    }
  };

  const onUpdate = async () => {
    setSaving(true);
    try {
      await adminTheaterApi.update(theaterId, buildPartialBody());
      alert("수정 완료");
      await load();
    } catch (e) {
      console.error(e);
      alert("수정에 실패했습니다.");
    } finally {
      setSaving(false);
    }
  };

  const onActivate = async () => {
    const ok = window.confirm("운영중(ACTIVE)으로 전환할까요?");
    if (!ok) return;

    setSaving(true);
    try {
      await adminTheaterApi.activate(theaterId);
      alert("ACTIVE 전환 완료");
      await load();
    } catch (e) {
      console.error(e);
      alert("ACTIVE 전환에 실패했습니다.");
    } finally {
      setSaving(false);
    }
  };

  const onHide = async () => {
    const ok = window.confirm("숨김(HIDDEN)으로 전환할까요?");
    if (!ok) return;

    setSaving(true);
    try {
      await adminTheaterApi.hide(theaterId);
      alert("HIDDEN 전환 완료");
      await load();
    } catch (e) {
      console.error(e);
      alert("숨김 처리에 실패했습니다.");
    } finally {
      setSaving(false);
    }
  };

  const onClose = async () => {
    const ok = window.confirm("운영 종료(CLOSED)로 전환할까요?");
    if (!ok) return;

    setSaving(true);
    try {
      await adminTheaterApi.close(theaterId);
      alert("CLOSED 전환 완료");
      await load();
    } catch (e) {
      console.error(e);
      alert("운영 종료 처리에 실패했습니다.");
    } finally {
      setSaving(false);
    }
  };

  const onRemove = async () => {
    const ok = window.confirm("삭제(DELETED) 처리할까요? (복구 불가)");
    if (!ok) return;

    setSaving(true);
    try {
      await adminTheaterApi.remove(theaterId);
      alert("삭제 처리 완료");
      navigate("/admin/theaters");
    } catch (e) {
      console.error(e);
      alert("삭제 처리에 실패했습니다.");
    } finally {
      setSaving(false);
    }
  };

  const onBack = () => navigate(-1);

  return (
    <div className="admin-theater-detail">
      <div className="admin-theater-detail__card">
        {/* ===== Top ===== */}
        <div className="admin-theater-detail__top">
          <div className="admin-theater-detail__title-wrap">
            <h2 className="admin-theater-detail__title">영화관 상세</h2>

            <div className="admin-theater-detail__meta">
              <span className="admin-theater-detail__id">ID: {theaterId}</span>

              <span className={badgeClass}>
                <span className="admin-theater-detail__dot" />
                {status}
              </span>
            </div>
          </div>

          <button className="admin-theater-detail__back-btn" onClick={onBack}>
            ← 목록/이전
          </button>
        </div>

        {/* ===== Content ===== */}
        <div className="admin-theater-detail__content">
          <h3 className="admin-theater-detail__section-title">기본 정보</h3>

          {loading ? (
            <div className="admin-theater-detail__loading">로딩 중...</div>
          ) : (
            <div className="admin-theater-detail__grid">
              <div className="admin-theater-detail__field">
                <label className="admin-theater-detail__label">
                  영화관 이름
                </label>
                <input
                  className="admin-theater-detail__input"
                  value={form.theaterName}
                  onChange={onChange("theaterName")}
                  disabled={isDeleted}
                  placeholder="예) Movieing 강남점"
                />
              </div>

              <div className="admin-theater-detail__field admin-theater-detail__field--full">
                <label className="admin-theater-detail__label">주소</label>
                <input
                  className="admin-theater-detail__input"
                  value={form.address}
                  onChange={onChange("address")}
                  disabled={isDeleted}
                  placeholder="예) 서울시 ..."
                />
              </div>

              <div className="admin-theater-detail__field">
                <label className="admin-theater-detail__label">위도(lat)</label>
                <input
                  className="admin-theater-detail__input"
                  value={form.lat}
                  onChange={onChange("lat")}
                  disabled={isDeleted}
                  placeholder="예) 37.4979"
                />
              </div>

              <div className="admin-theater-detail__field">
                <label className="admin-theater-detail__label">경도(lng)</label>
                <input
                  className="admin-theater-detail__input"
                  value={form.lng}
                  onChange={onChange("lng")}
                  disabled={isDeleted}
                  placeholder="예) 127.0276"
                />
              </div>

              <div className="admin-theater-detail__field">
                <label className="admin-theater-detail__label">
                  영업 시작 시간
                </label>
                <input
                  className="admin-theater-detail__input"
                  value={form.openTime}
                  onChange={onChange("openTime")}
                  disabled={isDeleted}
                  placeholder="예) 09:00"
                />
              </div>

              <div className="admin-theater-detail__field">
                <label className="admin-theater-detail__label">
                  영업 종료 시간
                </label>
                <input
                  className="admin-theater-detail__input"
                  value={form.closeTime}
                  onChange={onChange("closeTime")}
                  disabled={isDeleted}
                  placeholder="예) 24:00"
                />
              </div>
            </div>
          )}

          {/* ===== Actions ===== */}
          <div className="admin-theater-detail__actions">
            {/* DRAFT 전용 */}
            {canEditDraft && (
              <>
                <button
                  className="admin-theater-detail__btn admin-theater-detail__btn--ghost"
                  onClick={onSaveDraft}
                  disabled={saving || loading}
                >
                  임시 저장
                </button>

                <button
                  className="admin-theater-detail__btn admin-theater-detail__btn--primary"
                  onClick={onComplete}
                  disabled={saving || loading}
                >
                  완료 처리
                </button>

                <button
                  className="admin-theater-detail__btn admin-theater-detail__btn--danger"
                  onClick={onRemove}
                  disabled={saving || loading}
                >
                  삭제
                </button>
              </>
            )}

            {/* ACTIVE/HIDDEN/CLOSED */}
            {canEditNormal && (
              <>
                <button
                  className="admin-theater-detail__btn admin-theater-detail__btn--primary"
                  onClick={onUpdate}
                  disabled={saving || loading}
                >
                  수정 저장
                </button>

                {status !== "ACTIVE" && (
                  <button
                    className="admin-theater-detail__btn admin-theater-detail__btn--ghost"
                    onClick={onActivate}
                    disabled={saving || loading}
                  >
                    ACTIVE 전환
                  </button>
                )}

                {status !== "HIDDEN" && (
                  <button
                    className="admin-theater-detail__btn admin-theater-detail__btn--ghost"
                    onClick={onHide}
                    disabled={saving || loading}
                  >
                    숨김
                  </button>
                )}

                {status !== "CLOSED" && (
                  <button
                    className="admin-theater-detail__btn admin-theater-detail__btn--ghost"
                    onClick={onClose}
                    disabled={saving || loading}
                  >
                    운영 종료
                  </button>
                )}

                <button
                  className="admin-theater-detail__btn admin-theater-detail__btn--danger"
                  onClick={onRemove}
                  disabled={saving || loading}
                >
                  삭제
                </button>
              </>
            )}

            {/* DELETED */}
            {isDeleted && (
              <div className="admin-theater-detail__hint">
                삭제(DELETED) 상태의 데이터는 수정할 수 없습니다.
              </div>
            )}
          </div>

          {!isDeleted && (
            <div className="admin-theater-detail__hint">
              {isDraft
                ? "DRAFT 상태에서는 임시 저장/완료만 가능합니다. 완료 시 ACTIVE로 전환됩니다."
                : "ACTIVE/HIDDEN/CLOSED 상태에서는 부분 수정이 가능하며, 상태 전이는 버튼으로만 처리됩니다."}
            </div>
          )}

          {/* ===== Screens Section (다음 작업) ===== */}
          <div className="admin-theater-detail__screens">
            <h3 className="admin-theater-detail__section-title">상영관</h3>
            <AdminScreenListPage theaterId={theaterId}/>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AdminTheaterDetailPage;
