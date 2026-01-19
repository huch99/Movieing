import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { adminBookingApi } from './adminBookingAPi';
import './AdminBookingDetailPage.css';

const AdminBookingDetailPage = () => {
    const navigate = useNavigate();
    const { bookingId } = useParams();

    const [bookingDetails, setBookingDetails] = useState(null);
    const [bookingLoading, setBookingLoading] = useState(false);

    const load = async () => {
        setBookingLoading(true);
        try {
            const data = await adminBookingApi.getDetail(bookingId);
            setBookingDetails(data?.data ?? null);
        } catch (e) {
            console.error(e);
        } finally {
            setBookingLoading(false);
        }
    }

    useEffect(() => {
        load();
    }, []);

    const onCancel = async () => {
        if (!window.confirm("취소 시 복구 할 수 없습니다. 취소 하시겠습니까?")) return;
        try {
            await adminBookingApi.cancelBooking(bookingId);
            navigate(-1);
        } catch (e) {
            console.error(e);
        }
    }

    return (
        <div className='admin-booking-detail'>
            <div className="admin-booking-detail__header">
                <h2 className="admin-booking-detail__title">상세 예매 내역</h2>
            </div>

            <div className="admin-booking-detail__actions">
                <button className="admin-booking-detail__backBtn" onClick={navigate(-1)}>목록</button>
                <button className="admin-booking-detail-cancelBtn" onClick={onCancel}>예매 취소</button>
            </div>

            <div className="admin-booking-detail__section-wrap">
                {bookingLoading && (
                    <div className="admin-booking-detail__loading">로딩 중...</div>
                )}

                {!bookingDetails ? (
                    <div className="admin-booking-detail__empty">데이터가 없습니다.</div>
                ) : (
                    <div className="booking-detail">
                        <div className="booking-detail__grid">
                            <div className="filed">
                                <label>예매 번호</label>
                                <input value={bookingDetails.bookingNo} disabled />
                            </div>

                            <div className="filed">
                                <label>영화</label>
                                <input value={bookingDetails.title} disabled />
                            </div>

                            <div className="filed">
                                <label>영화관 - 상영관</label>
                                <input value={`${bookingDetails.theaterName} - ${bookingDetails.screenName}`} disabled />
                            </div>

                            <div className="filed">
                                <label>예매일</label>
                                <input value={bookingDetails.scheduledDate} disabled />
                            </div>

                            <div className="filed">
                                <label>상영 시간</label>
                                <input value={`${bookingDetails.startAt} ~ ${bookingDetails.endAt}`} disabled />
                            </div>

                            <div className="filed">
                                <label>예매 좌석</label>
                                <input value={bookingDetails.seatNo} disabled />
                            </div>

                            <div className="filed">
                                <label>예매인</label>
                                <input value={bookingDetails.userName} disabled />
                            </div>

                            <div className="filed">
                                <label>예매인 이메일 - 연락처</label>
                                <input value={`${bookingDetails.email} - ${bookingDetails.phone}`} disabled />
                            </div>
                            
                            <div className="filed">
                                <label>총 결제 금액</label>
                                <input value={bookingDetails.totalAmount} disabled />
                            </div>

                            <div className="filed">
                                <label>예매 상태</label>
                                <input value={bookingDetails.status} disabled />
                            </div>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
};

export default AdminBookingDetailPage;