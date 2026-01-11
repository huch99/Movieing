export type ApiResultCode = "SUCCESS" | "ERROR";

export type ApiResponse<T> = {
  resultCode: ApiResultCode;
  resultMessage: string;
  data: T | null;
};

export type UserRole = "USER" | "ADMIN" | "THEATER";

export type User = {
  publicUserId: string;
  userName: string;
  email: string;
  role: UserRole;
};