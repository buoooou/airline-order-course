package com.postion.airlineorderbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequestDTO {

    @Schema(description = "用户名", required = true)
    private String username;
    
    @Schema(description = "密码", required = true)
    private String password;
}
