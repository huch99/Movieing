import api from "../../../shared/api/client";

const BASE = "/admin/movies";

function unwrap(body) {
  const b = body?.data ?? body; // axios response or body 둘 다 방어
  if (b?.resultCode === "SUCCESS") return b.data;
  throw new Error(b?.resultMessage || "요청 실패");
}

export const adminMovieApi = {
  // ✅ 영화 초안 생성 (body optional)
  async createDraft(initial = null) {
    const res = await api.post(BASE, initial ?? null);
    return unwrap(res); // Long movieId
  },

  // ✅ 목록 조회 (Page)
  async getList(params) {
    // PageableDefault가 있지만 프론트에서 명시하면 더 안정적
    const res = await api.get(BASE, {params});
    return unwrap(res); // Page<MovieListItem>
  },

  // ✅ 상세 조회
  async getDetail(movieId) {
    const res = await api.get(`${BASE}/${movieId}`);
    return unwrap(res); // MovieDetail
  },

  // ✅ 임시 저장 (DRAFT에서만)
  async saveDraft(movieId, body) {
    const res = await api.put(`${BASE}/${movieId}/draft`, body);
    return unwrap(res); // void
  },

  // ✅ 완료 처리 (DRAFT -> COMING_SOON)
  async complete(movieId, body) {
    const res = await api.put(`${BASE}/${movieId}/complete`, body);
    return unwrap(res); // void
  },

  // ✅ 수정 (부분 수정)
  async update(movieId, body) {
    const res = await api.put(`${BASE}/${movieId}`, body);
    return unwrap(res); // void
  },

  // ✅ 숨김
  async hide(movieId) {
    const res = await api.put(`${BASE}/${movieId}/hide`);
    return unwrap(res);
  },

  // ✅ 숨김 해제
  async unhide(movieId) {
    const res = await api.put(`${BASE}/${movieId}/unhide`);
    return unwrap(res);
  },

  // ✅ 삭제 (소프트 삭제)
  async remove(movieId) {
    const res = await api.delete(`${BASE}/${movieId}`);
    return unwrap(res);
  },

  getStats: async() => {
    const res = await api.get(`${BASE}/stats`);
    return res.data.data;
  }
};