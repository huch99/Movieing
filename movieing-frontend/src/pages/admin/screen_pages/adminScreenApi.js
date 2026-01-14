import api from "../../../shared/api/client";

const BASE = "/admin/screens";

function unwrap(body) {
    const b = body?.data ?? body; // axios response or body 둘 다 방어
    if (b?.resultCode === "SUCCESS") return b.data;
    throw new Error(b?.resultMessage || "요청 실패");
}

export const adminScreenApi = {

    // 특정 영화관의 상영관 목록 조회
    async getListByTheater(theaterId) {
        if (!theaterId) throw new Error("theaterId is required");
        const res = await api.get(`${BASE}/${theaterId}/getList`)
        return unwrap(res);
    },

    // 특정 영화관에 상영관 초안 생성
    async createDraftByTheater(theaterId) {
        if (!theaterId) throw new Error("theaterId is required");

        const res = await api.post(`${BASE}/${theaterId}/screens/draft`);
        return unwrap(res); // Long screenId
    },

    // 상영관 상세 조회
    async getDetail(screenId) {
        const res = await api.get(`${BASE}/${screenId}`);
        return unwrap(res);
    },

    // 상영관 임시 저장
    async saveDraft(screenId, body) {
        const res = await api.put(`${BASE}/${screenId}/draft`, body);
        return unwrap(res);
    },

    // 상영관 완료 처리
    async complete(screenId, body) {
        const res = await api.post(`${BASE}/${screenId}/complete`, body);
        return unwrap(res);
    },

    // 상영관 수정
    async update(screenId, body) {
        const res = await api.put(`${BASE}/${screenId}`, body);
        return unwrap(res);
    },

    // ✅ 운영중(ACTIVE) 전환
    async activate(screenId) {
        const res = await api.post(`${BASE}/${screenId}/activate`);
        unwrap(res); // void
    },

    // ✅ 숨김(HIDDEN) 전환
    async hide(screenId) {
        const res = await api.post(`${BASE}/${screenId}/hide`);
        unwrap(res); // void
    },

    // ✅ 운영 종료(CLOSED) 전환
    async close(screenId) {
        const res = await api.post(`${BASE}/${screenId}/close`);
        unwrap(res); // void
    },

    // ✅ 삭제 (소프트 삭제)
    async remove(screenId) {
        const res = await api.delete(`${BASE}/${screenId}`);
        unwrap(res); // void
    },
}