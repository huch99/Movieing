import './Header.css';

type HeaderProps = {
    isLoggedIn?: boolean;
    userName?: string;
    onClickLogin?: () => void;
    onClickSignup?: () => void;
    onClickLogout?: () => void;
}

export default function Header({
    isLoggedIn = false,
    userName,
    onClickLogin,
    onClickSignup,
    onClickLogout,
}: HeaderProps) {
    return (
        <header className="app-header">
            <div className="app-header__inner">
                {/* Brand */}
                <div className="app-header__brand">
                    <img src="/EditedMovieingLogo.png" alt="Movieing Logo" className="app-header__logo" />
                </div>

                {/* Search */}
                <div className="app-header__search" role='search'>
                    <input
                        placeholder='영화, 극장, 배우 검색'
                        className="app-header__search-input"
                        aria-label='영화 검색'
                    />
                    <button className="app-header__search-btn" type='button'>
                        검색
                    </button>
                </div>

                {/* Actions */}
                <div className="app-header__actions">
                    {isLoggedIn ? (
                        <>
                            <span className="app-header__welcome">
                                {userName ? `${userName}님` : "환영합니다"}
                            </span>
                            <button
                                className="app-header__btn"
                                type='button'
                                onClick={onClickLogout}
                            >
                                로그아웃
                            </button>
                        </>
                    ) : (
                        <>
                            <button
                                className="app-header__btn"
                                type='button'
                                onClick={onClickLogin}
                            >
                                로그인
                            </button>
                            <button
                                className="app-header__btn app-header__btn--primary"
                                type='button'
                                onClick={onClickSignup}
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