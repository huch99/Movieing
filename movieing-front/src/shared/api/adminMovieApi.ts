import { type ApiResponse, type MovieCompleteAdminRequestDto, type MovieDetailAdminResponseDto, type MovieDraftSaveAdminRequestDto, type MovieListItemAdminResponseDto } from "../auth/types";
import api from "./api";

const BASE = "/admin/movies";

export type Page<T> = {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
};

export const adminMovieApi = {
    async createDraft(initial?: MovieDraftSaveAdminRequestDto) {
        const res = await api.post<ApiResponse<number>>(BASE, initial ?? {});
        return res.data.data ?? [];
    },

    async getList(params?: { page?: number; size?: number }) {
        const res = await api.get<ApiResponse<MovieListItemAdminResponseDto[]>>(BASE, {params});
        return res.data.data ?? [];
    },

    async getDetail(movieId: number): Promise<MovieDetailAdminResponseDto> {
        const res = await api.get<ApiResponse<MovieDetailAdminResponseDto>>(`${BASE}/${movieId}`);

        if (!res.data.data) {
            throw new Error("영화 정보를 불러올 수 없습니다.");
        }

        return res.data.data;
    },

    async saveDraft(movieId: number, body: MovieDraftSaveAdminRequestDto) {
        await api.put<ApiResponse<null>>(`${BASE}/${movieId}/draft`, body);
    },

    async complete(movieId: number, body: MovieCompleteAdminRequestDto) {
        await api.put<ApiResponse<null>>(`${BASE}/${movieId}/complete`, body);
    },

    async update(movieId: number) {
        await api.put<ApiResponse<null>>(`${BASE}/${movieId}/hide`);
    },

    async unhide(movieId: number) {
        await api.put<ApiResponse<null>>(`${BASE}/${movieId}/unhide`);
    },

    async remove(movieId: number) {
        await api.delete<ApiResponse<null>>(`${BASE}/${movieId}`);
    },
}
