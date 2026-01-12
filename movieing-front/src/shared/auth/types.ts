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

export type MovieStatus =
  | "DRAFT"
  | "COMING_SOON"
  | "NOW_SHOWING"
  | "HIDDEN"
  | "ENDED"
  | "DELETED";

export type MovieListItemAdminResponseDto = {
  movieId: number;
  title: string;
  posterUrl: string;
  releaseDate: string | null; // YYYY-MM-DD
  endDate: string | null;     // YYYY-MM-DD
  status: MovieStatus;
};

export type MovieDetailAdminResponseDto = {
  movieId: number;
  title: string | null;
  synopsis: string | null;
  director: string | null;
  genre: string | null;
  runtimeMin: number | null;
  releaseDate: string | null;
  endDate: string | null;
  rating: string | null;
  posterUrl: string | null;
  status: MovieStatus;
};

// 요청 DTO들
export type MovieDraftSaveAdminRequestDto = Partial<{
  title: string;
  synopsis: string;
  director: string;
  genre: string;
  runtimeMin: number;
  releaseDate: string; // YYYY-MM-DD
  endDate: string;     // YYYY-MM-DD
  rating: string;
  posterUrl: string;
}>;

export type MovieCompleteAdminRequestDto = {
  title: string;
  synopsis: string;
  releaseDate: string;
  endDate: string;
  runtimeMin: number;
  rating: string;
  director?: string;
  genre?: string;
  posterUrl?: string;
};

export type MovieUpdateAdminRequestDto = MovieDraftSaveAdminRequestDto;
