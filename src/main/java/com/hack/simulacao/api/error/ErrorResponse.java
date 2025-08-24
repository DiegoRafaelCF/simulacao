package com.hack.simulacao.api.error;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String error;
    private String message;
    private String path;
    private long timestamp;

    public ErrorResponse(int status, String error, String message, String path, List<String> errors) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.timestamp = System.currentTimeMillis();
    }

    public ErrorResponse(int status, String error, String message, String path) {
        this(status, error, message, path, null);
    }
}
