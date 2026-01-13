import React, { useEffect, useMemo, useState } from 'react';
import './css/AdminSideBar.css';
import { NavLink, useLocation } from 'react-router-dom';

const adminMenus = [
    { label: "대시보드", to: "/admin", end: true },
    { label: "영화 관리", to: "/admin/movies" },
    { label: "영화관/상영관", to: "/admin/theaters" },
    { label: "좌석 관리", to: "/admin/seats" },
    { label: "상영 스케줄", to: "/admin/schedules" },
    { label: "예매/결제", to: "/admin/bookings" },
    { label: "사용자 관리", to: "/admin/users" },
    { label: "콘텐츠 관리", to: "/admin/content" },
    { label: "설정", to: "/admin/settings" },
];

const AdminSideBar = () => {

    const location = useLocation();

    const isTheaterSection = useMemo(() => {
        const p = location.pathname;
        return p.startsWith('/admin/theaters') || p.startsWith('/admin/screens');
    }, [location.pathname]);

    const [openTheaterSub, setOpenTheaterSub] = useState(false);

    // ✅ 하위 메뉴 페이지로 직접 들어오면 자동 오픈
    useEffect(() => {
        if (isTheaterSection) setOpenTheaterSub(true);
    }, [isTheaterSection]);

    return (
        <aside className="admin-sidebar">
            <div className="admin-sidebar__top">
                <div className="admin-sidebar__title">ADMIN</div>
                <div className="admin-sidebar__sub">Movieing 운영</div>
            </div>

            <nav className="admin-sidebar__nav" aria-label="관리자 메뉴">
                {adminMenus.map((m) => {
                    const isTheaterMenu = m.label === "영화관/상영관";

                    // ✅ 영화관/상영관: 클릭 시 이동 X, 하위 메뉴만 토글
                    if (isTheaterMenu) {
                        return (
                            <div key={m.label} className={`admin-sidebar__group ${openTheaterSub ? "is-open" : ""}`}>
                                <div
                                    type="button"
                                    className={`admin-sidebar__link ${isTheaterSection ? "is-active" : ""}`}
                                    onClick={() => setOpenTheaterSub((v) => !v)}
                                    aria-expanded={openTheaterSub}
                                    aria-controls="admin-sidebar-theater-submenu"
                                >
                                    {m.label}
                                    <span className="admin-sidebar__chevron" aria-hidden="true">▾</span>
                                </div>

                                {openTheaterSub && (
                                    <div id="admin-sidebar-theater-submenu" className="admin-sidebar__submenu">
                                        <NavLink
                                            to="/admin/theaters"
                                            end
                                            className={({ isActive }) =>
                                                `admin-sidebar__sublink ${isActive ? "is-active" : ""}`
                                            }
                                        >
                                            영화관 관리
                                        </NavLink>

                                        <NavLink
                                            to="/admin/screens"
                                            className={({ isActive }) =>
                                                `admin-sidebar__sublink ${isActive ? "is-active" : ""}`
                                            }
                                        >
                                            상영관 관리
                                        </NavLink>
                                    </div>
                                )}
                            </div>
                        );
                    }

                    // ✅ 나머지는 기존대로 NavLink
                    return (
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
                    );
                })}
            </nav>

            <div className="admin-sidebar__bottom">
                <div className="admin-sidebar__hint">권한이 필요한 기능입니다.</div>
            </div>
        </aside>
    );
};

export default AdminSideBar;