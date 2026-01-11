import { useMemo } from 'react';
import './Header.css';

type HeaderVariant = "app" | "admin";

type HeaderProps = {
    variant?: HeaderVariant;
    isLoggedIn?: boolean;
    userName?: string;
    onClickLogin?: () => void;
    onClickSignup?: () => void;
    onClickLogout?: () => void;
    roleLabel?: string;
}

export default function Header({
    variant = "app",
    isLoggedIn,
    userName,
    onClickLogin,
    onClickSignup,
    onClickLogout,
    roleLabel,
}: HeaderProps) {

    const isAdmin = variant === "admin";

    const welcomeText = useMemo(() => {
        if (!isLoggedIn) return "";
        return userName ? `${userName}님` : "로그인됨";
    }, [isLoggedIn, userName]);

    return (
        <header className={`app-header ${isAdmin ? "app-header--admin" : ""}`}>
            <div className="app-header__inner">
                {/* Brand */}
                <div className="app-header__brand">
                    <img
                        className="app-header__logo"
                        src="/EditedMovieingLogo.png"
                        alt="Movieing"
                        onClick={() => {
                            // app이면 /app, admin이면 /admin으로 이동
                            window.location.href = isAdmin ? "/admin" : "/app";
                        }}
                    />
                </div>

                {/* Search: app에서만 노출 (admin에서는 숨김) */}
                {!isAdmin && (
                    <div className="app-header__search">
                        <input
                            className="app-header__search-input"
                            placeholder="영화, 극장, 지역을 검색해보세요"
                        />
                        <button className="app-header__search-btn" type="button">
                            검색
                        </button>
                    </div>
                )}

                {/* Actions */}
                <div className="app-header__actions">
                    {isLoggedIn ? (
                        <>
                            {/* admin 배지 */}
                            {isAdmin && (
                                <span className="app-header__badge">
                                    {roleLabel ? `${roleLabel} 콘솔` : "관리자 콘솔"}
                                </span>
                            )}

                            <span className="app-header__welcome">{welcomeText}</span>

                            {/* admin일 때 사용자 화면으로 */}
                            {isAdmin && (
                                <a className="app-header__link" href="/app">
                                    사용자 화면
                                </a>
                            )}

                            <button className="app-header__btn" onClick={onClickLogout} type="button">
                                로그아웃
                            </button>
                        </>
                    ) : (
                        <>
                            <button className="app-header__btn" onClick={onClickLogin} type="button">
                                로그인
                            </button>
                            <button
                                className="app-header__btn app-header__btn--primary"
                                onClick={onClickSignup}
                                type="button"
                            >
                                회원가입
                            </button>
                        </>
                    )}
                </div>
            </div>
        </header>
    );
}