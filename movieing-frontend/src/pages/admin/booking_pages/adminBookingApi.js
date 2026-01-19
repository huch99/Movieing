import api from "../../../shared/api/client";

const BASE = "/admin/bookings";

export const adminBookingApi = {

    getList: async(params) => {
        const res = await api.get(BASE, { params });
        return res.data;
    },

    getDetail: async(bookingId) => {
        const res = await api.get(`${BASE}/${bookingId}/detail`);
        return res.data;
    },

    cancelBooking: async(bookingId) => {
        const res = await api.put(`${BASE}/${bookingId}/cancel`);
        return res.data;
    }
};