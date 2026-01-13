import React, { useEffect, useMemo, useState } from 'react';
import './css/LoginModal.css';
import api, { ACCESS_TOKEN_KEY } from "../../shared/api/client";

const LoginModal = ({ open, onClose, onSuccess }) => {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");

    const [loading, setLoading] = useState(false);
    const [errorMsg, setErrorMsg] = useState(null);

    const canSubmit = useMemo(() => {
        return email.trim().length > 0 && password.trim().length > 0 && !loading;
    }, [email, password, loading]);

    // 모달 열릴 때마다 초기화
    useEffect(() => {
        if (!open) return;
        setEmail("");
        setPassword("");
        setErrorMsg(null);
        setLoading(false);
    }, [open]);

    // ESC 닫기
    useEffect(() => {
        if (!open) return;

        const onKeyDown = (e) => {
            if (e.key === "Escape") onClose();
        };

        window.addEventListener("keydown", onKeyDown);
        return () => window.removeEventListener("keydown", onKeyDown);
    }, [open, onClose]);

    const handleOverlayClick = (e) => {
        // 오버레이 클릭 시 닫기 (모달 박스 클릭은 무시)
        if (e.target === e.currentTarget) onClose();
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!canSubmit) return;

        try {
            setLoading(true);
            setErrorMsg(null);

            const res = await api.post("/auth/login", { email, password });

            const body = res?.data ?? res;
            const ok = body?.resultCode === "SUCCESS";
            const user = body?.data;

            if (!ok || !user) {
                throw new Error(body?.message || "로그인에 실패했습니다.");
            }

            // accessToken 저장
            localStorage.setItem(ACCESS_TOKEN_KEY, user.accessToken);

            // 성공 콜백
            if (onSuccess) {
                onSuccess(user);
                onClose();
                return;
            }

            // 기본 동작: ADMIN/THEATER면 /admin, 아니면 /
            window.location.href = user.role === "ADMIN" || user.role === "THEATER" ? "/admin" : "/";
        } catch (err) {
            const msg =
                err?.response?.data?.resultMessage ||
                err?.response?.data?.message ||
                err?.message ||
                "로그인 중 오류가 발생했습니다.";
            setErrorMsg(msg);
            setErrorMsg(msg);
        } finally {
            setLoading(false);
        }
    };

    if (!open) return null;

    return (
        <div
            className="login-modal__overlay"
            onMouseDown={handleOverlayClick}
            role="presentation"
        >
            <div className="login-modal" role="dialog" aria-modal="true" aria-label="로그인">
                <div className="login-modal__header">
                    <div className="login-modal__brand">
                        <img className="login-modal__logo" src="/MovieingLogo.png" alt="Movieing" />
                        <span className="login-modal__title">로그인</span>
                    </div>

                    <button
                        type="button"
                        className="login-modal__close"
                        onClick={onClose}
                        aria-label="닫기"
                    >
                        ✕
                    </button>
                </div>

                <form className="login-modal__form" onSubmit={handleSubmit}>
                    <label className="login-modal__label">
                        이메일
                        <input
                            className="login-modal__input"
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            placeholder="admin@movieing.com"
                            autoComplete="email"
                        />
                    </label>

                    <label className="login-modal__label">
                        비밀번호
                        <input
                            className="login-modal__input"
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            placeholder="비밀번호를 입력하세요"
                            autoComplete="current-password"
                        />
                    </label>

                    {errorMsg && <div className="login-modal__error">{errorMsg}</div>}

                    <button className="login-modal__submit" type="submit" disabled={!canSubmit}>
                        {loading ? "로그인 중..." : "로그인"}
                    </button>

                    <div className="login-modal__hint">
                        계정이 없나요?{" "}
                        <button type="button" className="login-modal__link">
                            회원가입
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default LoginModal;