package com.postion.airlineorderbackend.dto;
import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class RegisterRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 20, message = "用户名长度必须在4-20个字符之间")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码长度不能少于6个字符")
    private String password;
    
    @NotBlank(message = "角色不能为空")
    @Email(message = "角色不正确")
    private String role;

//    @NotBlank(message = "邮箱不能为空")
//    @Email(message = "邮箱格式不正确")
//    private String email;
//
//    @NotBlank(message = "手机号不能为空")
//    @Size(min = 11, max = 11, message = "手机号必须为11位")
//    private String phone;
}
    