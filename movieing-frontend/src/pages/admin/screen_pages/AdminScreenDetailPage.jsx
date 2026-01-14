import React, { useEffect, useState } from "react";
import "./AdminScreenDetailPage.css";
import { useNavigate, useParams } from "react-router-dom";
import { adminScreenApi } from "./adminScreenApi";
import AdminSeatLayoutPage from "../seat_pages/AdminSeatLayoutPage";

const STATUS_LABEL = {
  DRAFT: "DRAFT",
  ACTIVE: "ACTIVE",
  HIDDEN: "HIDDEN",
  CLOSED: "CLOSED",
  DELETED: "DELETED",
};

const AdminScreenDetailPage = () => {
  const { screenId } = useParams();
  const navigate = useNavigate();

  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [data, setData] = useState(null);

  const [form, setForm] = useState({
    screenName: "",
    capacity: "",
    seatRowCount: "",
    seatColCount: "",
  });

  /* ================= load ================= */
  const load = async () => {
    setLoading(true);
    try {
      const d = await adminScreenApi.getDetail(screenId);
      setData(d);
      setForm({
        screenName: d.screenName ?? "",
        capacity: d.capacity ?? "",
        seatRowCount: d.seatRowCount ?? "",
        seatColCount: d.seatColCount ?? "",
      });
    } catch (e) {
      alert("상영관 정보를 불러오지 못했습니다.");
      navigate(-1);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
    // eslint-disable-next-line
  }, [screenId]);

  /* ================= handlers ================= */
  const onChange = (e) => {
    const { name, value } = e.target;
    setForm((f) => ({ ...f, [name]: value }));
  };

  const onSaveDraft = async () => {
    try {
      setSaving(true);
      await adminScreenApi.saveDraft(screenId, {
        screenName: form.screenName || null,
        capacity: form.capacity !== "" ? Number(form.capacity) : null,
        seatRowCount:
          form.seatRowCount !== "" ? Number(form.seatRowCount) : null,
        seatColCount:
          form.seatColCount !== "" ? Number(form.seatColCount) : null,
      });

      alert("임시 저장되었습니다.");
      load();
    } catch {
      alert("임시 저장 실패");
    } finally {
      setSaving(false);
    }
  };

  const onComplete = async () => {
    if (
      !form.screenName ||
      form.capacity === "" ||
      form.seatRowCount === "" ||
      form.seatColCount === ""
    ) {
      alert("상영관 이름, 수용 인원, 좌석 행/열 수는 필수입니다.");
      return;
    }

    if (!data?.theaterId) {
      alert("영화관 정보가 없습니다.");
      return;
    }

    try {
      setSaving(true);
      await adminScreenApi.complete(screenId, {
        theaterId: data.theaterId,
        screenName: form.screenName,
        capacity: Number(form.capacity),
        seatRowCount: Number(form.seatRowCount),
        seatColCount: Number(form.seatColCount),
      });

      alert("상영관이 완료되었습니다.");
      load();
    } catch {
      alert("완료 처리 실패");
    } finally {
      setSaving(false);
    }
  };

  const onUpdate = async () => {
    try {
      setSaving(true);
      await adminScreenApi.update(screenId, {
        screenName: form.screenName || null,
        capacity: form.capacity !== "" ? Number(form.capacity) : null,
        seatRowCount:
          form.seatRowCount !== "" ? Number(form.seatRowCount) : null,
        seatColCount:
          form.seatColCount !== "" ? Number(form.seatColCount) : null,
      });

      alert("수정되었습니다.");
      load();
    } catch {
      alert("수정 실패");
    } finally {
      setSaving(false);
    }
  };

  const onDelete = async () => {
    if (!window.confirm("삭제할까요?")) return;
    try {
      await adminScreenApi.remove(screenId);
      alert("삭제되었습니다.");
      navigate(-1);
    } catch {
      alert("삭제 실패");
    }
  };

  /* ================= render ================= */
  if (loading || !data) return null;

  const isDraft = data.status === "DRAFT";

  return (
    <div className="admin-screen-detail">
      <div className="admin-screen-detail__card">
        <h2>상영관 상세</h2>
        <p>
          ID #{data.screenId} · 상태 {data.status}
        </p>

        <div className="admin-screen-detail__grid">
          <div>
            <label>상영관 이름</label>
            <input
              name="screenName"
              value={form.screenName}
              onChange={onChange}
              disabled={data.status === "DELETED"}
            />
          </div>

          <div>
            <label>수용 인원</label>
            <input
              type="number"
              name="capacity"
              value={form.capacity}
              onChange={onChange}
              disabled={data.status === "DELETED"}
            />
          </div>

          <div>
            <label>좌석 행 수</label>
            <input
              type="number"
              name="seatRowCount"
              value={form.seatRowCount}
              onChange={onChange}
              disabled={data.status === "DELETED"}
              min={1}
            />
          </div>

          <div>
            <label>좌석 열 수</label>
            <input
              type="number"
              name="seatColCount"
              value={form.seatColCount}
              onChange={onChange}
              disabled={data.status === "DELETED"}
              min={1}
            />
          </div>
        </div>

        <div className="admin-screen-detail__actions">
          {isDraft && (
            <>
              <button onClick={onSaveDraft}>임시 저장</button>
              <button onClick={onComplete}>완료</button>
            </>
          )}

          {!isDraft && data.status !== "DELETED" && (
            <button onClick={onUpdate}>수정</button>
          )}

          {data.status !== "DELETED" && (
            <button className="danger" onClick={onDelete}>
              삭제
            </button>
          )}
        </div>
      </div>

      <div>
        <div>좌석 배치도</div>
        <div>
          <AdminSeatLayoutPage screenId={screenId} />
        </div>
      </div>
    </div>
  );
};

export default AdminScreenDetailPage;
