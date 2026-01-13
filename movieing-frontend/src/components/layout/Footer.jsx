import React from 'react';
import './css/Footer.css';

const Footer = () => {
    return (
        <footer className="app-footer">
            <div className="app-footer__inner">
                <div className="app-footer__top">
                    <div className="app-footer__brand">
                        <img
                            src="/EditedMovieingLogo.png"
                            alt="Movieing"
                            className="app-footer__logo"
                        />
                    </div>

                    <nav className="app-footer__links" aria-label="footer links">
                        <a className="app-footer__link" href="#">
                            이용약관
                        </a>
                        <span className="app-footer__dot">•</span>
                        <a className="app-footer__link" href="#">
                            개인정보처리방침
                        </a>
                        <span className="app-footer__dot">•</span>
                        <a className="app-footer__link" href="#">
                            고객센터
                        </a>
                    </nav>
                </div>

                <div className="app-footer__meta">
                    <div>© {new Date().getFullYear()} Movieing. All rights reserved.</div>
                    <div className="app-footer__sub">
                        본 프로젝트는 개인 포트폴리오 용도로 제작되었습니다.
                    </div>
                </div>
            </div>
        </footer>
    );
};

export default Footer;