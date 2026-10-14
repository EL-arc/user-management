package com.example.usermanagement;

public class Result<T> {
    private final int code;
    private final String message;
    private final T data;


    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
