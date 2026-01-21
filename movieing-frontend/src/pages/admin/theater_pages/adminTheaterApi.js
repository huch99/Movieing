// src/features/admin/theater/api/adminTheaterApi.js
import api from "../../../shared/api/client";

const BASE = "/admin/theaters";

function unwrap(body) {
  const b = body?.data ?? body; // axios response or body 둘 다 방어
  if (b?.resultCode === "SUCCESS") return b.data;
  throw new Error(b?.resultMessage || "요청 실패");
}

export const adminTheaterApi = {
  // ✅ 영화관 목록 조회
  // GET /api/admin/theaters
  async getList(params) {
    const res = await api.get(BASE, {params});
    return unwrap(res); // List<TheaterListItemAdminResponseDto>
  },

  // ✅ 영화관 초안(DRAFT) 생성
  // POST /api/admin/theaters/draft
  async createDraft() {
    const res = await api.post(`${BASE}/draft`);
    return unwrap(res); // Long theaterId
  },

  // ✅ 영화관 상세 조회
  // GET /api/admin/theaters/{theaterId}
  async getDetail(theaterId) {
    const res = await api.get(`${BASE}/${theaterId}`);
    return unwrap(res); // TheaterDetailAdminResponseDto
  },

  // ✅ 영화관 임시 저장 (DRAFT에서만)
  // PUT /api/admin/theaters/{theaterId}/draft
  async saveDraft(theaterId, body) {
    const res = await api.put(`${BASE}/${theaterId}/draft`, body);
    unwrap(res); // void
  },

  // ✅ 영화관 완료 처리 (DRAFT -> ACTIVE)
  // POST /api/admin/theaters/{theaterId}/complete
  async complete(theaterId, body) {
    const res = await api.post(`${BASE}/${theaterId}/complete`, body);
    unwrap(res); // void
  },

  // ✅ 영화관 수정 (부분 수정)
  // PUT /api/admin/theaters/{theaterId}
  async update(theaterId, body) {
    const res = await api.put(`${BASE}/${theaterId}`, body);
    unwrap(res); // void
  },

  // ✅ 운영중(ACTIVE) 전환
  // POST /api/admin/theaters/{theaterId}/activate
  async activate(theaterId) {
    const res = await api.post(`${BASE}/${theaterId}/activate`);
    unwrap(res); // void
  },

  // ✅ 숨김(HIDDEN) 전환
  // POST /api/admin/theaters/{theaterId}/hide
  async hide(theaterId) {
    const res = await api.post(`${BASE}/${theaterId}/hide`);
    unwrap(res); // void
  },

  // ✅ 운영 종료(CLOSED) 전환
  // POST /api/admin/theaters/{theaterId}/close
  async close(theaterId) {
    const res = await api.post(`${BASE}/${theaterId}/close`);
    unwrap(res); // void
  },

  // ✅ 삭제 (소프트 삭제)
  // DELETE /api/admin/theaters/{theaterId}
  async remove(theaterId) {
    const res = await api.delete(`${BASE}/${theaterId}`);
    unwrap(res); // void
  },

  getStats: async() => {
    const res = await api.get(`${BASE}/stats`);
    return res.data.data;
  }
};
