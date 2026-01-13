import React from 'react';
import './css/AdminLayout.css';
import Header from '../layout/Header';
import { Navigate, Outlet, useNavigate } from 'react-router-dom';
import Footer from '../layout/Footer';
import { ACCESS_TOKEN_KEY } from '../../shared/api/client';
import AdminSideBar from './AdminSideBar';

const AdminLayout = ({ user, setUser }) => {
    const navigate = useNavigate();

    // ✅ 로그인 안 했으면 / 로
    if (!user) return <Navigate to="/" replace />;

    // ✅ ADMIN / THEATER만 접근 허용
    const isAdmin = user.role === "ADMIN" || user.role === "THEATER";
    if (!isAdmin) return <Navigate to="/app" replace />;

    const handleLogout = () => {
        localStorage.removeItem(ACCESS_TOKEN_KEY);
        if (setUser) setUser(null);
        navigate("/app", { replace: true });
    };

    return (
        <div className="admin-layout">
            <Header
                variant="admin"
                isLoggedIn={true}
                userName={user.userName}
                roleLabel={user.role}
                onClickLogin={() => navigate("/app", { replace: true })}
                onClickSignup={() => navigate("/app", { replace: true })}
                onClickLogout={handleLogout}
            />

            <div className="admin-layout__grid">
                <AdminSideBar />
                <div className="admin-layout__content">
                    <Outlet />
                </div>
            </div>

            <Footer />
        </div>
    );
};

export default AdminLayout;