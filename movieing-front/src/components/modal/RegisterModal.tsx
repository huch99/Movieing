import React, { useEffect, useMemo, useState } from "react";
import api from "../../shared/api/api";
import "./RegisterModal.css";

type UserRole = "USER" | "ADMIN" | "THEATER";

type RegisterResponseDto = {
    publicUserId: string;
    userName: string;
    email: string;
    role: UserRole;
};

type ApiResponse<T> = {
    resultCode: "SUCCESS" | "ERROR";
    resultMessage: string;
    data: T | null;
};

type RegisterModalProps = {
    open: boolean;
    onClose: () => void;

    /** 회원가입 성공 후 동작 */
    onSuccess?: (user: RegisterResponseDto) => void;
};

export default function RegisterModal({ open, onClose, onSuccess }: RegisterModalProps) {
    const [userName, setUserName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [passwordConfirm, setPasswordConfirm] = useState("");

    const [loading, setLoading] = useState(false);
    const [errorMsg, setErrorMsg] = useState<string | null>(null);

    const canSubmit = useMemo(() => {
        return (
            userName.trim().length > 0 &&
            email.trim().length > 0 &&
            password.trim().length > 0 &&
            passwordConfirm.trim().length > 0 &&
            !loading
        );
    }, [userName, email, password, passwordConfirm, loading]);

    // 모달 열릴 때마다 초기화
    useEffect(() => {
        if (!open) return;
        setUserName("");
        setEmail("");
        setPassword("");
        setPasswordConfirm("");
        setErrorMsg(null);
        setLoading(false);
    }, [open]);

    // ESC 닫기
    useEffect(() => {
        if (!open) return;

        const onKeyDown = (e: KeyboardEvent) => {
            if (e.key === "Escape") onClose();
        };

        window.addEventListener("keydown", onKeyDown);
        return () => window.removeEventListener("keydown", onKeyDown);
    }, [open, onClose]);

    const handleOverlayClick = (e: React.MouseEvent<HTMLDivElement>) => {
        if (e.target === e.currentTarget) onClose();
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!canSubmit) return;

        if (password !== passwordConfirm) {
            setErrorMsg("비밀번호가 일치하지 않습니다.");
            return;
        }

        try {
            setLoading(true);
            setErrorMsg(null);

            // ✅ 백엔드 DTO/엔드포인트에 맞게 바꾸면 됨
            const res = await api.post<ApiResponse<RegisterResponseDto>>("/auth/register", {
                userName,
                email,
                password,
            });

            if (res.data.resultCode !== "SUCCESS" || !res.data.data) {
                throw new Error(res.data.resultMessage || "회원가입에 실패했습니다.");
            }

            const user = res.data.data;

            onSuccess?.(user);

            // 기본 동작: 닫기
            onClose();
        } catch (err: any) {
            const msg =
                err?.response?.data?.resultMessage ||
                err?.message ||
                "회원가입 중 오류가 발생했습니다.";
            setErrorMsg(msg);
        } finally {
            setLoading(false);
        }
    };

    if (!open) return null;

    return (
        <div className="register-modal__overlay" onMouseDown={handleOverlayClick} role="presentation">
            <div className="register-modal" role="dialog" aria-modal="true" aria-label="회원가입">
                <div className="register-modal__header">
                    <div className="register-modal__brand">
                        <img className="register-modal__logo" src="/MovieingLogo.png" alt="Movieing" />
                        <span className="register-modal__title">회원가입</span>
                    </div>

                    <button type="button" className="register-modal__close" onClick={onClose} aria-label="닫기">
                        ✕
                    </button>
                </div>

                <form className="register-modal__form" onSubmit={handleSubmit}>
                    <label className="register-modal__label">
                        이름
                        <input
                            className="register-modal__input"
                            value={userName}
                            onChange={(e) => setUserName(e.target.value)}
                            placeholder="홍길동"
                            autoComplete="name"
                        />
                    </label>

                    <label className="register-modal__label">
                        이메일
                        <input
                            className="register-modal__input"
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            placeholder="user@movieing.com"
                            autoComplete="email"
                        />
                    </label>

                    <label className="register-modal__label">
                        비밀번호
                        <input
                            className="register-modal__input"
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            placeholder="비밀번호를 입력하세요"
                            autoComplete="new-password"
                        />
                    </label>

                    <label className="register-modal__label">
                        비밀번호 확인
                        <input
                            className="register-modal__input"
                            type="password"
                            value={passwordConfirm}
                            onChange={(e) => setPasswordConfirm(e.target.value)}
                            placeholder="비밀번호를 다시 입력하세요"
                            autoComplete="new-password"
                        />
                    </label>

                    {errorMsg && <div className="register-modal__error">{errorMsg}</div>}

                    <button className="register-modal__submit" type="submit" disabled={!canSubmit}>
                        {loading ? "가입 중..." : "회원가입"}
                    </button>

                    <div className="register-modal__hint">
                        이미 계정이 있나요? <button type="button" className="register-modal__link">로그인</button>
                    </div>
                </form>
            </div>
        </div>
    );
}
