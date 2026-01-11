import { useEffect, useState } from "react";
import Footer from "./Footer";
import Header from "./Header";
import "./Layout.css";
import SideBar from "./SideBar";
import { ACCESS_TOKEN_KEY } from "../../shared/api/api";
import LoginModal from "../modal/LoginModal";
import { Outlet } from "react-router-dom";
import SignupModal from "../modal/SignupModal";

type UserRole = "USER" | "ADMIN" | "THEATER";

type User = {
    publicUserId: string;
    userName: string;
    email: string;
    role: UserRole;
};


export default function Layout() {

    const [loginOpen, setLoginOpen] = useState(false);
    const [registerOpen, setRegisterOpen] = useState(false);
    const [user, setUser] = useState<User | null>(null);

    useEffect(() => {
        const token = localStorage.getItem(ACCESS_TOKEN_KEY);
        if (!token) return;

        // 토큰만 있으면 일단 로그인 UI로 처리 (userName 없으면 기본 문구로)
        setUser((prev) =>
            prev ?? { publicUserId: "", userName: "사용자", email: "", role: "USER" }
        );
    }, []);

    const handleLogout = () => {
        localStorage.removeItem(ACCESS_TOKEN_KEY);
        setUser(null);
        window.location.href = "/";
    };

    return (
        <div className="layout">
            <Header
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

                    // ADMIN이면 /admin 이동
                    if (u.role === "ADMIN") {
                        window.location.href = "/admin";
                    }
                }}
            />

            {/* ✅ 회원가입 모달 추가 */}
            <SignupModal
                open={registerOpen}
                onClose={() => setRegisterOpen(false)}
                onSuccess={() => {
                    setRegisterOpen(false);
                    setLoginOpen(true);
                }}
            />
        </div >
    );
}
