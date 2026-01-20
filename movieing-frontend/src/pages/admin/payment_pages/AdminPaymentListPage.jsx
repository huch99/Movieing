import React, { useEffect, useState } from 'react';
import './AdminPaymentListPage.css';
import { useNavigate } from 'react-router-dom';
import { adminPaymentApi } from './adminPaymentApi';

const AdminPaymentListPage = () => {
    const navigate = useNavigate();

    const [page, setPage] = useState(0);
    const size = 20;

    const [status, setStatus] = useState("ALL");

    const [payments, setPayments] = useState([]);
    const [paymentLoading, setPaymentLoading] = useState(false);

    const load = async (nextPage = page, nextStatus = status) => {
        setPaymentLoading(true);
        try {
            const params = {
                page: nextPage,
                size,
                sort: "publicPaymentId,desc",
                ...(nextStatus ? { status: nextStatus } : {})
            }

            const data = await adminPaymentApi.getList(params);
            setPayments(data?.content ?? []);
        } catch (e) {
            console.error(e);
        } finally {
            setPaymentLoading(false);
        }
    };

    useEffect(() => {
        setPage(0);
        load(0, status);
    }, [])

    const onRefunded = async (paymentId) => {
        if (!window.confirm("정말로 환불 하시겠습니까?")) return;

        try {
            await adminPaymentApi.refunded(paymentId);
            load();
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
        <div className='admin-payment-list'>
            <div className="admin-payment-list__card">
                <div className="admin-payment-list__top">
                    <div className="admin-payment-list__title-wrap">
                        <h2 className="admin-payment-list__title">결제 관리</h2>
                        {/* <p className="admin-booking-list__subtitle"></p> */}
                    </div>
                </div>
            </div>

            <div className="admin-payment-list__table-wrap">
                <table className="admin-payment-list__table">
                    <thead className="admin-payment-list__thead">
                        <tr>
                            <th>결제 번호</th>
                            <th>예매 번호</th>
                            <th>이름</th>
                            <th>결제 금액</th>
                            <th>결제 시간</th>
                            <th>상태</th>
                            <th></th>
                        </tr>
                    </thead>

                    <tbody className="admin-payment-list__tbody">
                        {payments.length === 0 ? (
                            <tr>
                                <td className="admin-payment-list__empty" colSpan={4}>
                                    결제 정보가 없습니다.
                                </td>
                            </tr>
                        ) : (
                            payments.map((m) => (
                                <tr
                                    key={m.paymentId}
                                    className="admin-payment-list-row"
                                    onClick={() => navigate(`/admin/payments/${m.paymentId}/detail`)}
                                >
                                    <td className='admin-payment-list__publicPaymentId'>{m.publicPaymentId}</td>
                                    <td className='admin-payment-list__bookingNo'>{m.bookingNo}</td>
                                    <td className='admin-payment-list__userName'>{m.userName}</td>
                                    <td className='admin-payment-list__amount'>{m.amount}원</td>
                                    <td className='admin-payment-list__approvedAt'>{m.approvedAt}</td>
                                    <td className="admin-payment-list__status">{m.status}</td>
                                    <td><button className="admin-payment-list__cancelBtn" onClick={(e) => {
                                        e.stopPropagation(); // ✅ 행 클릭(navigate) 막기
                                        onRefunded(m.paymentId);
                                    }}>환불</button></td>
                                </tr>
                            )))}
                    </tbody>
                </table>
            </div>

            {/* Pager */}
            <div className="pager">
                <button onClick={goPrev} disabled={paymentLoading || page <= 0}>
                    이전
                </button>
                <div className="pager__info">
                    {payments ? `${page + 1} / ${payments.totalPages}` : "-"}
                </div>
                <button
                    onClick={goNext}
                    disabled={paymentLoading || !payments || page + 1 >= payments.totalPages}
                >
                    다음
                </button>
            </div>

        </div>
    );
};

export default AdminPaymentListPage;