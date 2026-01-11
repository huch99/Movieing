package com.movieing.movieingbackend.aspect;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

    private ResultCode resultCode;
    private String resultMessage;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .resultCode(ResultCode.SUCCESS)
                .resultMessage("SUCCESS")
                .data(data)
                .build();
    }
    public static <T> ApiResponse<T> error(String resultMessage) {
        return ApiResponse.<T>builder()
                .resultCode(ResultCode.ERROR)
                .resultMessage(resultMessage)
                .data(null)
                .build();
    }
}
