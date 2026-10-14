package com.example.usermanagement;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(UserOperationException.class)
    public ResponseEntity<Result<Void>> handleUserOperation(UserOperationException exception) {
        return error(exception.getStatus(), exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getDefaultMessage())
                .distinct().sorted().collect(Collectors.joining("；"));
        return error(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Result<Void>> handleInvalidBody(HttpMessageNotReadableException exception) {
        return error(HttpStatus.BAD_REQUEST, "请求体必须是有效的 JSON，且字段类型必须正确");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Result<Void>> handleInvalidId(MethodArgumentTypeMismatchException exception) {
        return error(HttpStatus.BAD_REQUEST, "用户 ID 必须为正整数，且不能超过 2147483647");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Result<Void>> handleUnknownPath(NoResourceFoundException exception) {
        return error(HttpStatus.NOT_FOUND, "接口不存在");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Result<Void>> handleInvalidMethod(HttpRequestMethodNotSupportedException exception) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .headers(exception.getHeaders())
                .body(new Result<>(405, "请求方法不支持", null));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Result<Void>> handleInvalidContentType(HttpMediaTypeNotSupportedException exception) {
        return error(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "请求体必须使用 application/json");
    }

    private ResponseEntity<Result<Void>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new Result<>(status.value(), message, null));
    }
}
