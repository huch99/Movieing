import api from "../../../shared/api/client";

const BASE = "/admin/seats";

function unwrap(resOrBody) {
  const b = resOrBody?.data ?? resOrBody; // axios response or body 둘 다 방어
  if (b?.resultCode === "SUCCESS") return b.data;
  throw new Error(b?.resultMessage || "요청 실패");
}

export const adminSeatApi = {
  // ✅ 좌석 배치도 조회 (screenId 기준)
  async getLayout(screenId) {
    const res = await api.get(BASE, { params: { screenId } });
    return unwrap(res); // List<SeatLayoutItemAdminResponseDto>
  },

  // ✅ 좌석 생성 (최초 생성)
  async generateSeats(screenId, body) {
    const res = await api.post(`${BASE}/${screenId}`, body); // SeatCreateAdminRequestDto
    unwrap(res); // void
  },

  // ✅ 좌석 재생성 (기존 좌석 물리삭제 후 재생성)
  async regenerateSeats(screenId, body) {
    const res = await api.post(`${BASE}/${screenId}/regenerate`, body); // SeatCreateAdminRequestDto
    unwrap(res); // void
  },

  // ✅ 좌석 배치도 생성 (최초 생성)
  async generateLayout(screenId, body) {
    const res = await api.post(`${BASE}/${screenId}/layout`, body); // SeatCreateAdminRequestDto
    unwrap(res); // void
  },

  // ✅ 좌석 배치도 재생성 (기존 좌석 물리삭제 후 재생성)
  async regenerateLayout(screenId, body) {
    const res = await api.post(`${BASE}/${screenId}/regenerate/layout`, body); // SeatCreateAdminRequestDto
    unwrap(res); // void
  },

  // ✅ 좌석 수정 (상태/위치)
  async updateSeat(seatId, body) {
    const res = await api.put(`${BASE}/${seatId}`, body); // SeatUpdateAdminRequestDto
    unwrap(res); // void
  },

  // ✅ 좌석 삭제 (물리 삭제)
  async deleteSeat(seatId) {
    const res = await api.delete(`${BASE}/${seatId}`);
    unwrap(res); // void
  },
};
