import { NavLink } from "react-router-dom";
import "./SideBar.css";

type SideBarProps = {
  isLoggedIn?: boolean;
  userName?: string;
  onLogout?: () => void;
};

export default function SideBar({ isLoggedIn = false, userName, onLogout }: SideBarProps) {
  return (
    <aside className="sidebar" aria-label="user sidebar">
      <div className="sidebar__inner">
        {/* Profile / Brand */}
        <div className="sidebar__top">
          <div className="sidebar__profile">
            <div className="sidebar__hello">
              {isLoggedIn ? (
                <>
                  <span className="sidebar__name">{userName ?? "사용자"}</span>
                  <span className="sidebar__suffix">님</span>
                </>
              ) : (
                "로그인 해주세요"
              )}
            </div>
            <div className="sidebar__sub">
              {isLoggedIn ? "즐거운 관람 되세요 🎬" : "예매/찜/내역은 로그인 후 이용 가능"}
            </div>
          </div>
        </div>

        {/* Nav */}
        <nav className="sidebar__nav">
          <div className="sidebar__section">
            <div className="sidebar__section-title">예매</div>

            <NavLink to="/" className={({ isActive }) => `sidebar__item ${isActive ? "active" : ""}`}>
              홈
            </NavLink>
            <NavLink to="/movies" className={({ isActive }) => `sidebar__item ${isActive ? "active" : ""}`}>
              영화
            </NavLink>
            <NavLink to="/timetable" className={({ isActive }) => `sidebar__item ${isActive ? "active" : ""}`}>
              상영시간표
            </NavLink>
            <NavLink to="/quick-book" className={({ isActive }) => `sidebar__item ${isActive ? "active" : ""}`}>
              빠른 예매
            </NavLink>
          </div>

          <div className="sidebar__divider" />

          <div className="sidebar__section">
            <div className="sidebar__section-title">MY Movieing</div>

            <NavLink
              to="/my/bookings"
              className={({ isActive }) => `sidebar__item ${isActive ? "active" : ""}`}
            >
              나의 예매
            </NavLink>
            <NavLink
              to="/my/payments"
              className={({ isActive }) => `sidebar__item ${isActive ? "active" : ""}`}
            >
              결제 내역
            </NavLink>
            <NavLink
              to="/my/bookmarks"
              className={({ isActive }) => `sidebar__item ${isActive ? "active" : ""}`}
            >
              찜한 영화
            </NavLink>
            <NavLink
              to="/my/recent"
              className={({ isActive }) => `sidebar__item ${isActive ? "active" : ""}`}
            >
              최근 본 영화
            </NavLink>
          </div>

          <div className="sidebar__divider" />

          <div className="sidebar__section">
            <div className="sidebar__section-title">극장</div>

            <NavLink
              to="/theaters"
              className={({ isActive }) => `sidebar__item ${isActive ? "active" : ""}`}
            >
              전체 극장
            </NavLink>
            <NavLink
              to="/theaters/favorites"
              className={({ isActive }) => `sidebar__item ${isActive ? "active" : ""}`}
            >
              선호 극장
            </NavLink>
          </div>

          <div className="sidebar__divider" />

          <div className="sidebar__section">
            <div className="sidebar__section-title">계정</div>

            <NavLink
              to="/my/profile"
              className={({ isActive }) => `sidebar__item ${isActive ? "active" : ""}`}
            >
              내 정보
            </NavLink>
            <NavLink
              to="/my/password"
              className={({ isActive }) => `sidebar__item ${isActive ? "active" : ""}`}
            >
              비밀번호 변경
            </NavLink>

            {isLoggedIn ? (
              <button className="sidebar__item sidebar__item--btn" type="button" onClick={onLogout}>
                로그아웃
              </button>
            ) : (
              <NavLink
                to="/login"
                className={({ isActive }) => `sidebar__item ${isActive ? "active" : ""}`}
              >
                로그인
              </NavLink>
            )}
          </div>

          <div className="sidebar__divider" />

          <div className="sidebar__section">
            <div className="sidebar__section-title">도움말</div>

            <NavLink
              to="/notice"
              className={({ isActive }) => `sidebar__item ${isActive ? "active" : ""}`}
            >
              공지사항
            </NavLink>
            <NavLink
              to="/support"
              className={({ isActive }) => `sidebar__item ${isActive ? "active" : ""}`}
            >
              고객센터
            </NavLink>
          </div>
        </nav>
      </div>
    </aside>
  );
}
