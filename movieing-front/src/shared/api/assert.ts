import type { ApiResponse } from "../auth/types";

export function unwrap<T>(res: ApiResponse<T>): T {
  if (res.resultCode !== "SUCCESS" || res.data === null) {
    throw new Error(res.resultMessage);
  }
  return res.data;
}