package com.example.shixun7.entity;

import lombok.Data;

@Data
public class ResponseResult {
    private int code;
    private String message;
    private Object data;

    public ResponseResult(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static ResponseResult success() {
        return new ResponseResult(200, "success", null);
    }

    public static ResponseResult success(Object data) {
        return new ResponseResult(200, "success", data);
    }

    public static ResponseResult error(String message) {
        return new ResponseResult(500, message, null);
    }

    // 无参 error 方法（兼容旧代码）
    public static ResponseResult error() {
        return new ResponseResult(500, "error", null);
    }
}