import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { getRoleFromToken, isTokenExpired } from "../shared/auth/token";
import MainLayout from "../components/layout/MainLayout";
import './Home.css';

const ACCESS_TOKEN_KEY = "DrOQDhqG7uWKi6zYDSObWSCuhcVtGsXBMaz8OPL1tuY=";

export default function Home() {
    const navigate = useNavigate();

    useEffect(() => {
        const token = localStorage.getItem(ACCESS_TOKEN_KEY);
        if (!token) return;

        if (isTokenExpired(token)) {
            localStorage.removeItem(ACCESS_TOKEN_KEY);
            return;
        }

        const role = getRoleFromToken(token);
        if (role?.toUpperCase() === "ADMIN" || "THEATER") navigate("/admin", { replace : true });
        else navigate("/app", {replace : true});
    }, [navigate])

  return (
    <MainLayout>
      <div className="home">
        <h1 className="home__title">영화 예매의 시작, Movieing</h1>
        <p className="home__desc">
          로그인 후 권한(role)에 따라 화면이 자동으로 분기됩니다.
          <br />
          ADMIN → 어드민 / 그 외 → 엔드유저
        </p>

        <div className="home__actions">
          <button className="home__btn" onClick={() => navigate("/login")}>
            로그인
          </button>
          <button className="home__btn home__btn--secondary" onClick={() => navigate("/signup")}>
            회원가입
          </button>
        </div>
      </div>
    </MainLayout>
  )
}