import React, { useEffect, useState } from 'react';
import { Outlet, useNavigate } from 'react-router-dom';
import { ACCESS_TOKEN_KEY } from '../../shared/api/client';
import './css/Layout.css';
import Header from './Header';
import Footer from './Footer';
import SideBar from './SideBar';
import LoginModal from '../modal/LoginModal';
import SignupModal from '../modal/SignupModal';

const Layout = ({ user, setUser }) => {
    const navigate = useNavigate();

    const [loginOpen, setLoginOpen] = useState(false);
    const [registerOpen, setRegisterOpen] = useState(false);

    useEffect(() => {
        const token = localStorage.getItem(ACCESS_TOKEN_KEY);
        if (!token) return;

        // 토큰만 있으면 일단 로그인 UI로 처리 (userName 없으면 기본 문구로)
        setUser((prev) =>
            prev ?? { publicUserId: "", userName: "사용자", email: "", role: "USER" }
        );
    }, [setUser]);

    const handleLogout = () => {
        localStorage.removeItem(ACCESS_TOKEN_KEY);
        setUser(null);
        window.location.href = "/";
    };

    return (
        <div className="layout">
            <Header
                variant="app"
                isLoggedIn={!!user}
                userName={user?.userName}
                onClickLogin={() => {
                    setRegisterOpen(false);
                    setLoginOpen(true);
                }}
                onClickSignup={() => {
                    setLoginOpen(false);
                    setRegisterOpen(true);
                }}
                onClickLogout={handleLogout}
            />

            <main className="layout__main">
                <div className="layout__grid">
                    <SideBar
                        isLoggedIn={!!user}
                        userName={user?.userName}
                        onLogout={handleLogout}
                    />

                    <div className="layout__content">
                        <Outlet />
                    </div>
                </div>
            </main>

            <Footer />

            {/* ✅ 로그인 모달은 Layout에서 관리 */}
            <LoginModal
                open={loginOpen}
                onClose={() => setLoginOpen(false)}
                onSuccess={(u) => {
                    // u는 LoginModal에서 넘겨주는 로그인 응답(user 정보 포함)
                    setUser({
                        publicUserId: u.publicUserId,
                        userName: u.userName,
                        email: u.email,
                        role: u.role,
                    });
                    setLoginOpen(false);

                    // ADMIN / THEATER면 /admin 이동
                    if (u.role === "ADMIN" || u.role === "THEATER") {
                        navigate("/admin", { replace: true });
                    }
                }}
            />

            {/* ✅ 회원가입 모달 */}
            <SignupModal
                open={registerOpen}
                onClose={() => setRegisterOpen(false)}
                onSuccess={() => {
                    setRegisterOpen(false);
                    setLoginOpen(true);
                }}
            />
        </div>
    );
};

export default Layout;