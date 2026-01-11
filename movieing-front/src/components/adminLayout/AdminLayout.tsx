import { Outlet, Navigate, useNavigate } from "react-router-dom";
import AdminSideBar from "./AdminSideBar";
import "./AdminLayout.css";
import { ACCESS_TOKEN_KEY } from "../../shared/api/api";
import Header from "../layout/Header";
import Footer from "../layout/Footer";
import type { User } from "../../shared/auth/types";

type AdminLayoutProps = {
    user: User | null;
    setUser?: React.Dispatch<React.SetStateAction<User | null>>;
};

export default function AdminLayout({ user, setUser }: AdminLayoutProps) {
    const navigate = useNavigate();
    // ✅ 로그인 안 했거나 ADMIN이 아니면 / 로 보냄(원하면 /app으로 바꿔도 됨)
    if (!user) return <Navigate to="/" replace />;
    const isAdmin = user.role === "ADMIN" || user.role === "THEATER";
    if (!isAdmin) return <Navigate to="/app" replace />;

    const handleLogout = () => {
        localStorage.removeItem(ACCESS_TOKEN_KEY);
        setUser?.(null);
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
}