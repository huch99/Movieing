import React, { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { adminBookingApi } from './adminBookingAPi';
import './AdminBookingListPage.css';


const STATUS_OPTIONS = [
    { value: "ALL", label: "전체" },
    { value: "FENDING", label: "대기" },
    { value: "CONFIRMED", label: "확정" },
    { value: "CANCELED", label: "취소" },
    { value: "EXPIRED", label: "만료" },
    { value: "FAILED", label: "실패" },
]

const AdminBookingListPage = () => {
    const navigate = useNavigate();

    const [page, setPage] = useState(0);
    const size = 20;

    const [bookings, setBookings] = useState([]);
    const [bookingLoading, setBookingLoading] = useState(false);

    const [q, setQ] = useState("");
    const [status, setStatus] = useState("ALL")

    const load = async (nextPage = page, nextStatus = status) => {
        setBookingLoading(true);

        try {
            const params = {
                page: nextPage,
                size,
                sort: "bookingNo,desc",
                ...(nextStatus ? { status: nextStatus } : {}),
            }

            const res = await adminBookingApi.getList(params);
            setBookings(res?.content ?? []);
        } catch (e) {
            console.error(e);
        } finally {
            setBookingLoading(false);
        }
    }

    useEffect(() => {
        setPage(0);
        load(0, status);
    }, []);

    const filtered = useMemo(() => {
        const keyword = q.trim().toLowerCase();

        return (bookings || [])
            .filter((b) => {
                if (status === "ALL") return true;
                return String(b.status) === status;
            })
            .filter((b) => {
                if (!keyword) return true;
                const hay = [
                    b.userName,
                    b.bookingNo,
                ]
                    .filter(Boolean)
                    .join(" ")
                    .toLowerCase();
                return hay.includes(keyword);
            })
    }, [bookings, status, q])

    const onCancel = async (bookingId) => {
        if (!window.confirm("취소 시 복구 할 수 없습니다. 취소 하시겠습니까?")) return;
        try {
            await adminBookingApi.cancelBooking(bookingId);
            await load();
        } catch (e) {
            console.error(e);
        }
    }

    const goPrev = () => {
        const next = Math.max(0, page - 1);
        setPage(next);
        load(next);
    }

    const goNext = () => {
        const totalPages = pageData?.totalPages ?? 0;
        const next = Math.min(totalPages - 1, page + 1);
        setPage(next);
        load(next);
    };

    return (
        <div className='admin-booking-list'>
            <div className="admin-booking-list__card">
                <div className="admin-booking-list__top">
                    <div className="admin-booking-list__title-wrap">
                        <h2 className="admin-booking-list__title">예매 관리</h2>
                        {/* <p className="admin-booking-list__subtitle"></p> */}
                    </div>
                </div>
            </div>

            <div className="admin-booking-list__fileters">
                <div className="admin-booking-list__filter">
                    <label>상태</label>
                    <select value={status}
                        onChange={(e) => setStatus(e.target.value)}
                    >
                        {STATUS_OPTIONS.map((o) => (
                            <option key={o.value} value={o.value}>
                                {o.label}
                            </option>
                        ))}
                    </select>
                </div>

                <div className="admin-booking-list__filter admin-movie-list__filter--grow">
                    <label>검색</label>
                    <input
                        value={q}
                        onChange={(e) => setQ(e.target.value)}
                        placeholder='이름 / 예매번호'
                    />
                </div>

                <div className="admin-booking-list__count">
                    총 <strong>{filtered.length}</strong>개
                </div>

            </div>

            <div className="admin-booking-list__table-wrap">
                <table className="admin-booking-list__table">
                    <thead className="admin-booking-list__thead">
                        <tr>
                            <th>예매 번호</th>
                            <th>예매 영화</th>
                            <th>예매인</th>
                            <th>결제 금액</th>
                            <th></th>
                        </tr>
                    </thead>

                    <tbody className="admin-booking-list__tbody">
                        {bookings.length === 0 ? (
                            <tr>
                                <td className="admin-booking-list__empty" colSpan={4}>
                                    예매 정보가 없습니다.
                                </td>
                            </tr>
                        ) : (
                            filtered.map((m) => (
                                <tr
                                    key={m.bookingId}
                                    className="admin-booking-list-row"
                                    onClick={() => navigate(`/admin/bookings/${m.bookingId}/detail`)}
                                >
                                    <td className='admin-booking-list__bookingNo'>{m.bookingNo}</td>
                                    <td className='admin-booking-list__title'>{m.title}</td>
                                    <td className='admin-booking-list__userName'>{m.userName}</td>
                                    <td className='admin-booking-list__totalAmount'>{m.totalAmount}원</td>
                                    <td><button className="admin-booking-list__cancelBtn" onClick={onCancel(m.bookingId)}>취소</button></td>
                                </tr>
                            )))}
                    </tbody>
                </table>
            </div>

            {/* Pager */}
            <div className="pager">
                <button onClick={goPrev} disabled={bookingLoading || page <= 0}>
                    이전
                </button>
                <div className="pager__info">
                    {bookings ? `${page + 1} / ${bookings.totalPages}` : "-"}
                </div>
                <button
                    onClick={goNext}
                    disabled={bookingLoading || !bookings || page + 1 >= bookings.totalPages}
                >
                    다음
                </button>
            </div>
        </div>
    );
};

export default AdminBookingListPage;