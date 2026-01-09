// src/shared/auth/token.ts
type JwtPayload = { role?: string; exp?: number; [k: string]: unknown };

export function parseJwtPayload(token: string): JwtPayload | null {
  try {
    const base64Url = token.split(".")[1];
    const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
    const json = decodeURIComponent(
      atob(base64)
        .split("")
        .map((c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
        .join("")
    );
    return JSON.parse(json);
  } catch {
    return null;
  }
}

export function getRoleFromToken(token: string): string | null {
  const payload = parseJwtPayload(token);
  return typeof payload?.role === "string" ? payload.role : null;
}

export function isTokenExpired(token: string): boolean {
  const payload = parseJwtPayload(token);
  const exp = payload?.exp;
  if (typeof exp !== "number") return false;
  const nowSec = Math.floor(Date.now() / 1000);
  return exp <= nowSec;
}
