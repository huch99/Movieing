import React, { useEffect, useMemo, useState } from 'react';
import './css/SignupModal.css';
import api from "../../shared/api/client";

const SignupModal = ({ open, onClose, onSuccess }) => {
    const [userName, setUserName] = useState("");
    const [email, setEmail] = useState("");
    const [phoneNumber, setPhoneNumber] = useState("");
    const [password, setPassword] = useState("");
    const [passwordConfirm, setPasswordConfirm] = useState("");

    const [loading, setLoading] = useState(false);
    const [errorMsg, setErrorMsg] = useState(null);

    const isValidPhoneNumber = useMemo(() => {
        const digits = phoneNumber.replace(/\D/g, "");
        return digits.length === 10 || digits.length === 11;
    }, [phoneNumber]);

    const canSubmit = useMemo(() => {
        return (
            userName.trim().length > 0 &&
            email.trim().length > 0 &&
            phoneNumber.trim().length > 0 &&
            password.trim().length > 0 &&
            passwordConfirm.trim().length > 0 &&
            isValidPhoneNumber &&
            !loading
        );
    }, [userName, email, phoneNumber, password, passwordConfirm, isValidPhoneNumber, loading]);

    useEffect(() => {
        if (!open) return;
        setUserName("");
        setEmail("");
        setPhoneNumber("");
        setPassword("");
        setPasswordConfirm("");
        setErrorMsg(null);
        setLoading(false);
    }, [open]);

    useEffect(() => {
        if (!open) return;

        const onKeyDown = (e) => {
            if (e.key === "Escape") onClose();
        };

        window.addEventListener("keydown", onKeyDown);
        return () => window.removeEventListener("keydown", onKeyDown);
    }, [open, onClose]);

    const handleOverlayClick = (e) => {
        if (e.target === e.currentTarget) onClose();
    };

    const formatPhone = (raw) => {
        const digits = raw.replace(/\D/g, "").slice(0, 11);
        if (digits.length <= 3) return digits;
        if (digits.length <= 7) return `${digits.slice(0, 3)}-${digits.slice(3)}`;
        if (digits.length === 10)
            return `${digits.slice(0, 3)}-${digits.slice(3, 6)}-${digits.slice(6)}`;
        return `${digits.slice(0, 3)}-${digits.slice(3, 7)}-${digits.slice(7)}`;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!canSubmit) return;

        if (password !== passwordConfirm) {
            setErrorMsg("비밀번호가 일치하지 않습니다.");
            return;
        }

        if (!isValidPhoneNumber) {
            setErrorMsg("전화번호 형식이 올바르지 않습니다.");
            return;
        }

        try {
            setLoading(true);
            setErrorMsg(null);

            const res = await api.post("/auth/signup", {
                userName,
                email,
                phone: phoneNumber.replace(/\D/g, ""),
                password,
            });

            // ApiResponse(success/data/message) 대응
            const body = res?.data ?? res;
            const ok = body?.success === true;
            const user = body?.data;

            if (!ok || !user) {
                throw new Error(body?.message || "회원가입에 실패했습니다.");
            }

            if (onSuccess) onSuccess(user);
            onClose();
        } catch (err) {
            const msg =
                err?.response?.data?.message ||
                err?.message ||
                "회원가입 중 오류가 발생했습니다.";
            setErrorMsg(msg);
        } finally {
            setLoading(false);
        }
    };

    if (!open) return null;

    return (
        <div
            className="signup-modal__overlay"
            onMouseDown={handleOverlayClick}
            role="presentation"
        >
            <div className="signup-modal" role="dialog" aria-modal="true" aria-label="회원가입">
                <div className="signup-modal__header">
                    <div className="signup-modal__brand">
                        <img
                            className="signup-modal__logo"
                            src="/EditedMovieingLogo.png"
                            alt="Movieing"
                        />
                        <span className="signup-modal__title">회원가입</span>
                    </div>

                    <button
                        type="button"
                        className="signup-modal__close"
                        onClick={onClose}
                        aria-label="닫기"
                    >
                        ✕
                    </button>
                </div>

                <form className="signup-modal__form" onSubmit={handleSubmit}>
                    <label className="signup-modal__label">
                        이름
                        <input
                            className="signup-modal__input"
                            value={userName}
                            onChange={(e) => setUserName(e.target.value)}
                            placeholder="홍길동"
                            autoComplete="name"
                        />
                    </label>

                    <label className="signup-modal__label">
                        이메일
                        <input
                            className="signup-modal__input"
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            placeholder="user@movieing.com"
                            autoComplete="email"
                        />
                    </label>

                    <label className="signup-modal__label">
                        전화번호
                        <input
                            className="signup-modal__input"
                            type="tel"
                            value={phoneNumber}
                            onChange={(e) => setPhoneNumber(formatPhone(e.target.value))}
                            placeholder="010-1234-5678"
                            autoComplete="tel"
                            inputMode="numeric"
                        />
                        {!isValidPhoneNumber && phoneNumber.trim().length > 0 && (
                            <div className="signup-modal__field-hint">
                                숫자 10~11자리로 입력해주세요.
                            </div>
                        )}
                    </label>

                    <label className="signup-modal__label">
                        비밀번호
                        <input
                            className="signup-modal__input"
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            placeholder="비밀번호를 입력하세요"
                            autoComplete="new-password"
                        />
                    </label>

                    <label className="signup-modal__label">
                        비밀번호 확인
                        <input
                            className="signup-modal__input"
                            type="password"
                            value={passwordConfirm}
                            onChange={(e) => setPasswordConfirm(e.target.value)}
                            placeholder="비밀번호를 다시 입력하세요"
                            autoComplete="new-password"
                        />
                    </label>

                    {errorMsg && <div className="signup-modal__error">{errorMsg}</div>}

                    <button className="signup-modal__submit" type="submit" disabled={!canSubmit}>
                        {loading ? "가입 중..." : "회원가입"}
                    </button>

                    <div className="signup-modal__hint">
                        이미 계정이 있나요?{" "}
                        <button type="button" className="signup-modal__link">
                            로그인
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default SignupModal;