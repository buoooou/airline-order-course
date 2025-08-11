package com.postion.airlineorderbackend.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.OffsetDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {
    private String errorCode;
    private String message;
    private int status;
    private String path;
    private OffsetDateTime timestamp = OffsetDateTime.now();
    private Map<String, Object> details;

    public ApiErrorResponse() {}

    public ApiErrorResponse(String errorCode, String message, int status, String path) {
        this.errorCode = errorCode;
        this.message = message;
        this.status = status;
        this.path = path;
    }

    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public OffsetDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(OffsetDateTime timestamp) { this.timestamp = timestamp; }
    public Map<String, Object> getDetails() { return details; }
    public void setDetails(Map<String, Object> details) { this.details = details; }
}
