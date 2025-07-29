package com.postion.airlineorderbackend.model;
import lombok.Data;
import javax.persistence.*;

import io.swagger.v3.oas.annotations.media.Schema;


@Entity
@Table(name = "app_users")
@Data
@Schema(description = "用户实体")
public class User {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Schema(description = "用户ID", example = "1")
    private Long id;
    
    @Schema(description = "用户名", example = "testuser")
    private String username;
    
    @Schema(description = "密码", example = "password123")
    private String password;
    
    @Schema(description = "用户角色", example = "USER", allowableValues = {"USER", "ADMIN"})
    private String role;
}
