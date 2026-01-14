import React from "react";
import "./css/AdminSideBar.css";
import { NavLink } from "react-router-dom";

const adminMenus = [
  { label: "대시보드", to: "/admin", end: true },
  { label: "영화 관리", to: "/admin/movies" },
  { label: "영화관/상영관/좌석", to: "/admin/theaters" },
  { label: "상영 스케줄", to: "/admin/schedules" },
  { label: "예매/결제", to: "/admin/bookings" },
  { label: "사용자 관리", to: "/admin/users" },
  { label: "콘텐츠 관리", to: "/admin/content" },
  { label: "설정", to: "/admin/settings" },
];

const AdminSideBar = () => {
  return (
    <aside className="admin-sidebar">
      <div className="admin-sidebar__top">
        <div className="admin-sidebar__title">ADMIN</div>
        <div className="admin-sidebar__sub">Movieing 운영</div>
      </div>

      <nav className="admin-sidebar__nav" aria-label="관리자 메뉴">
        {adminMenus.map((m) => (
          <NavLink
            key={m.to}
            to={m.to}
            end={m.end}
            className={({ isActive }) =>
              `admin-sidebar__link ${isActive ? "is-active" : ""}`
            }
          >
            {m.label}
          </NavLink>
        ))}
      </nav>

      <div className="admin-sidebar__bottom">
        <div className="admin-sidebar__hint">권한이 필요한 기능입니다.</div>
      </div>
    </aside>
  );
};

export default AdminSideBar;
