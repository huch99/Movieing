import api from "../../../shared/api/client";

const BASE = "/admin/schedules";

export const adminScheduleApi = {
    // 영화관 목록 (ACTIVE, HIDDEN)
    getTheaters: async () => {
        const res = await api.get(`${BASE}/theaters`);
        return res.data;
    },

    // 영화 목록
    getMovies: async (params) => {
        const res = await api.get(`${BASE}/movies`, { params });
        return res.data;
    },

    // 스케줄 목록 (Page)
    getList: async (params) => {
        // params 예: { page: 0, size: 10, sort: "scheduleId,desc" }
        const res = await api.get(BASE, { params });
        return res.data;
    },

    // 스케줄 상세
    getDetail: async (scheduleId) => {
        const res = await api.get(`${BASE}/${scheduleId}`);
        return res.data;
    },

    // 스케줄 임시 저장 (DRAFT)
    saveDraft: async (body) => {
        // body: { movieId?, scheduledDate?, startAt? }
        const res = await api.post(`${BASE}/draft`, body);
        return res.data; // scheduleId (Long)
    },

    // 스케줄 완료 (OPEN)
    complete: async (scheduleId, body) => {
        // body: { movieId, scheduledDate, startAt }
        const res = await api.post(`${BASE}/${scheduleId}/complete`, body);
        return res.data;
    },

    // 스케줄 수정 (OPEN only)
    update: async (scheduleId, body) => {
        // body: { movieId, scheduledDate, startAt }
        const res = await api.put(`${BASE}/${scheduleId}`, body);
        return res.data;
    },

    // 스케줄 취소
    cancel: async (scheduleId) => {
        const res = await api.post(`${BASE}/${scheduleId}/cancel`);
        return res.data;
    },

    // 스케줄 삭제(소프트 삭제)
    remove: async (scheduleId) => {
        const res = await api.delete(`${BASE}/${scheduleId}`);
        return res.data;
    },
}