import axios from "axios";

export const ACCESS_TOKEN_KEY =
  "DrOQDhqG7uWKi6zYDSObWSCuhcVtGsXBMaz8OPL1tuY=";

function getAccessToken() {
  return localStorage.getItem(ACCESS_TOKEN_KEY);
}

function clearAccessToken() {
  localStorage.removeItem(ACCESS_TOKEN_KEY);
}

/**
 * 인증 실패(401) 공통 처리
 * - 토큰 제거
 * - 로그인 페이지(또는 홈)로 이동
 */
function handleUnauthorized() {
  clearAccessToken();

  // 라우터 훅(useNavigate)은 여기서 못 쓰므로 강제 이동
  if (typeof window !== "undefined") {
    window.location.href = "/";
    // 필요하면 "/login"으로 변경
  }
}

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  withCredentials: false, // 쿠키 인증 쓰면 true
  headers: {
    "Content-Type": "application/json",
  },
  timeout: 15000,
});

// ✅ Request Interceptor: Authorization 헤더 자동 첨부
api.interceptors.request.use(
  (config) => {
    const token = getAccessToken();
    if (token) {
      config.headers = config.headers || {};
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// ✅ Response Interceptor: 공통 에러 처리
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error?.response?.status;

    // 인증 실패 / 만료
    if (status === 401) {
      handleUnauthorized();
    }

    return Promise.reject(error);
  }
);

/* =====================================================
 * 헬퍼 함수들 (타입 제거, JS 전용)
 * ===================================================== */

/**
 * GET 요청
 */
export async function get(url, config) {
  const res = await api.get(url, config);
  return res.data;
}

/**
 * POST 요청
 */
export async function post(url, body, config) {
  const res = await api.post(url, body, config);
  return res.data;
}


/**
 * PUT 요청
 */
export async function put(url, body, config) {
  const res = await api.put(url, body, config);
  return res.data;
}

/**
 * DELETE 요청
 */
export async function del(url, config) {
  const res = await api.delete(url, config);
  return res.data;
}

export default api;