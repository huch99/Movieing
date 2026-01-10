import axios from "axios";

import type {
  AxiosError,
  AxiosInstance,
  AxiosRequestConfig,
  AxiosResponse,
} from "axios";

export const ACCESS_TOKEN_KEY = "DrOQDhqG7uWKi6zYDSObWSCuhcVtGsXBMaz8OPL1tuY=";

function getAccessToken(): string | null {
  return localStorage.getItem(ACCESS_TOKEN_KEY);
}

function clearAccessToken() {
  localStorage.removeItem(ACCESS_TOKEN_KEY);
}

/**
 * 필요하면 여기서 refresh 토큰 로직으로 확장 가능
 */
function handleUnauthorized() {
  clearAccessToken();

  // 라우터 훅(useNavigate)은 여기서 못 쓰니까, 가장 단순한 방식으로 처리
  if (typeof window !== "undefined") {
    window.location.href = "/"; // 원하면 "/login"으로 변경
  }
}

const api: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  withCredentials: false, // 쿠키 기반이면 true로 변경
  headers: {
    "Content-Type": "application/json",
  },
  timeout: 15000,
});

// ✅ Request Interceptor: 토큰 자동 첨부
api.interceptors.request.use(
  (config) => {
    const token = getAccessToken();
    if (token) {
      config.headers = config.headers ?? {};
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// ✅ Response Interceptor: 공통 에러 처리
api.interceptors.response.use(
  (response) => response,
  (error: AxiosError) => {
    const status = error.response?.status;

    // 만료/인증 실패
    if (status === 401) {
      handleUnauthorized();
    }

    return Promise.reject(error);
  }
);

/**
 * 타입 안전 헬퍼들 (선택이지만 편함)
 */
export async function get<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
  const res: AxiosResponse<T> = await api.get(url, config);
  return res.data;
}

export async function post<T, B = unknown>(
  url: string,
  body?: B,
  config?: AxiosRequestConfig
): Promise<T> {
  const res: AxiosResponse<T> = await api.post(url, body, config);
  return res.data;
}

export async function put<T, B = unknown>(
  url: string,
  body?: B,
  config?: AxiosRequestConfig
): Promise<T> {
  const res: AxiosResponse<T> = await api.put(url, body, config);
  return res.data;
}

export async function del<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
  const res: AxiosResponse<T> = await api.delete(url, config);
  return res.data;
}

export default api;
