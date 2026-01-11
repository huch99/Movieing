import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom"
import Home from "./pages/Home/Home"
import './App.css';
import Layout from "./components/layout/Layout";
import AdminLayout from "./components/adminLayout/AdminLayout";
import AdminDashboard from "./pages/Admin/AdminDashboard";
import { useEffect, useState } from "react";
import api, { ACCESS_TOKEN_KEY } from "./shared/api/api";
import type { ApiResponse, User, UserRole } from "./shared/auth/types";

type TokenPayload = {
  role?: UserRole;
  sub?: string;
};

function decodeBase64Url(str: string) {
  const base64 = str.replace(/-/g, "+").replace(/_/g, "/");
  const padded = base64.padEnd(base64.length + (4 - (base64.length % 4)) % 4, "=");
  return atob(padded);
}


export function getTokenPayload(): TokenPayload | null {
  try {
    const token = localStorage.getItem(ACCESS_TOKEN_KEY);
    if (!token) return null;

    const payloadBase64 = token.split(".")[1];
    const decoded = decodeBase64Url(payloadBase64);
    return JSON.parse(decoded);
  } catch {
    return null;
  }
}


function App() {
  const [user, setUser] = useState<User | null>(() => {
    const token = localStorage.getItem(ACCESS_TOKEN_KEY);
    if (!token) return null;

    const payload = getTokenPayload();
    const role = payload?.role;
    if (!role) return null;

    return {
      publicUserId: payload?.sub ?? "",
      userName: "사용자",
      email: "",
      role,
    };
  });

  useEffect(() => {
    if (!user) return;

    (async () => {
      try {
        const res = await api.get<ApiResponse<User>>("/auth/me");

        if (res.data.resultCode === "SUCCESS" && res.data.data) {
          setUser(res.data.data); // 🎉 이름/이메일 실제 값으로 덮어쓰기
        }
      } catch {
        // 401이면 interceptor가 토큰 제거 + 리다이렉트 처리
      }
    })();
  }, []);

  return (
    <BrowserRouter>
      <Routes>
        {/* 기본 진입 */}
        <Route path="/" element={<Navigate to="/app" replace />} />

        {/* ✅ 유저 영역 */}
        <Route element={<Layout user={user} setUser={setUser} />}>
          <Route path="/app" element={<Home />} />
        </Route>

        {/* ✅ 어드민 영역 */}
        <Route path="/admin" element={<AdminLayout user={user} setUser={setUser} />}>
          <Route index element={<AdminDashboard />} />
          {/* <Route path="movies" element={<AdminMovies />} /> */}
          {/* /admin/theaters, /admin/schedules ... */}
        </Route>

        {/* 404 */}
        <Route path="*" element={<Navigate to="/app" replace />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
