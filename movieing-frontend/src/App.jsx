import { useEffect, useState } from 'react'
import api, { ACCESS_TOKEN_KEY } from "./shared/api/client";
import './App.css'
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import Layout from './components/layout/Layout';
import MainHome from './pages/MainHome';
import AdminDashBoard from './pages/admin/AdminDashBoard/AdminDashBoard';
import AdminLayout from './components/admin_layout/AdminLayout';
import AdminMovieListPage from './pages/admin/movie_pages/AdminMovieListPage';
import AdminMovieDetailPage from './pages/admin/movie_pages/AdminMovieDetailPage';

function decodeBase64Url(str) {
  const base64 = str.replace(/-/g, "+").replace(/_/g, "/");
  const padded = base64.padEnd(base64.length + (4 - (base64.length % 4)) % 4, "=");
  return atob(padded);
}

export function getTokenPayload() {
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
  const [user, setUser] = useState(() => {
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
    const token = localStorage.getItem(ACCESS_TOKEN_KEY);
    if (!token) return;

    (async () => {
      try {
        const res = await api.get("/auth/me");
        const body = res?.data ?? res;

        if (body?.resultCode === "SUCCESS" && body?.data) {
          setUser(body.data);
        }
      } catch { }
    })();
  }, []);

  return (
    <BrowserRouter>
      <Routes>
        {/* 기본 진입 */}
        <Route path="/" element={<Navigate to="/app" replace />} />

        {/* ✅ 유저 영역 */}
        <Route element={<Layout user={user} setUser={setUser} />}>
          <Route path="/app" element={<MainHome />} />
        </Route>

        {/* ✅ 어드민 영역 */}
        <Route path="/admin" element={<AdminLayout user={user} setUser={setUser} />}>
          <Route index element={<AdminDashBoard />} />
          <Route path="/admin/movies" element={<AdminMovieListPage />} />
          <Route path="/admin/movies/:movieId" element={<AdminMovieDetailPage />} />
        </Route>

        {/* 404 */}
        <Route path="*" element={<Navigate to="/app" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App
