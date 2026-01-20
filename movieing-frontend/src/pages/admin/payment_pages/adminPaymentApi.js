import api from "../../../shared/api/client";

const BASE = "/admin/payments";

export const adminPaymentApi = {

    getList: async(params) => {
        const res = await api.get(`${BASE}/getList`, params);
        return res.data;
    },

    getDetail: async(paymentId) => {
        const res = await api.get(`${BASE}/${paymentId}/detail`);
        return res.data;
    },

    refunded: async(paymentId) => {
        const res = await api.put(`${BASE}/${paymentId}/refunded`);
        return res.data;
    }
};