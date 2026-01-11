export const ADMIN_ROLES = ["ADMIN", "THEATER"] as const;

export const isAdminRole = (role?: string) =>
  role !== undefined && ADMIN_ROLES.includes(role as any);