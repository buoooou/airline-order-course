package com.postion.airlineorderbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "用户注册请求")
@Data
public class RegisterRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}

