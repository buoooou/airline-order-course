package com.postion.airlineorderbackend.dto.response;

import org.springframework.http.ResponseEntity;

import lombok.Data;

@Data
public class CommonResponseDto<T> {
  private boolean success;

  private int code;

  private String message;

  private T data;

  public CommonResponseDto() {
    setSuccess(true);
    setCode(200);
    setMessage("");
    setData(null);
  }

  public CommonResponseDto(boolean success, int code, String message, T data) {
    setSuccess(success);
    setCode(code);
    setMessage(message);
    setData(data);
  }

  public ResponseEntity<CommonResponseDto<T>> ok() {
    return ResponseEntity.ok().body(this);
  }

  public ResponseEntity<CommonResponseDto<T>> badRequest() {
    return ResponseEntity.badRequest().body(this);
  }

}
