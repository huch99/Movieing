import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { adminPaymentApi } from './adminPaymentApi';
import './AdminPaymentDetailPage.css';

const AdminPaymentDetailPage = () => {
    const navigate = useNavigate();
    const { paymentID } = useParams();

    const [paymentDetails, setPaymentDetails] = useState(null);
    const [paymentDetailLoading, setPaymentDeatilLoading] = useState(false);

    const load = async() => {
        setPaymentDeatilLoading(true);
        try {
            const data = await adminPaymentApi.getDetail(paymentID);
            setPaymentDetails(data ?? null);
        } catch (e) {
            console.error(e);
        } finally {
            setPaymentDeatilLoading(false);
        }
    };

    useEffect(() => {
        load();
    }, []);

    return (
        <div>
            
        </div>
    );
};

export default AdminPaymentDetailPage;