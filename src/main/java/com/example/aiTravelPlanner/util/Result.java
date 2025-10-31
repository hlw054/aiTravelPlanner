package com.example.aiTravelPlanner.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {
    /**
     * 状态码
     */
    private Integer code;

    /**
     * 返回的提示信息
     */
    private String msg;

    /**
     * 携带的数据
     */
    private T data;

    public static <T> Result<T> success(T data) {
        return new Result<>(0, "success", data);
    }

    public static <T> Result<T> success() {
        return new Result<>(0, "success", null);
    }

    public static <T> Result<T> success(Integer code, T data) {
        return new Result<>(code, "success", data);
    }

    public static <T> Result<T> error(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }
}