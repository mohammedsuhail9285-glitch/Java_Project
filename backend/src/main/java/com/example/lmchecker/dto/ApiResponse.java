package com.example.lmchecker.dto;

import java.time.Instant;
import java.util.Map;

public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private String errorCode;
    private String timestamp;
    private String path;
    private Map<String, String> validationErrors;

    public ApiResponse() {
        this.timestamp = Instant.now().toString();
    }

    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = Instant.now().toString();
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, "Operation successful", data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }

    public static <T> ApiResponse<T> error(String message, String errorCode) {
        ApiResponse<T> resp = new ApiResponse<>(false, message, null);
        resp.setErrorCode(errorCode);
        return resp;
    }

    public static <T> ApiResponse<T> error(String message, String errorCode, String path) {
        ApiResponse<T> resp = new ApiResponse<>(false, message, null);
        resp.setErrorCode(errorCode);
        resp.setPath(path);
        return resp;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public Map<String, String> getValidationErrors() { return validationErrors; }
    public void setValidationErrors(Map<String, String> validationErrors) { this.validationErrors = validationErrors; }
}
