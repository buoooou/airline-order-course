package com.postion.airlineorderbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AuthRequestDTO {

    @Schema(description = "用户名", required = true)
    private String username;
    
    @Schema(description = "密码", required = true)
    private String password;
}
