package com.postion.airlineorderbackend.controller;

import com.postion.airlineorderbackend.dto.UserDto;
import com.postion.airlineorderbackend.exception.BusinessException;
import com.postion.airlineorderbackend.model.Result;
import com.postion.airlineorderbackend.service.UserService;
import com.postion.airlineorderbackend.service.impl.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "认证管理", description = "用户登录接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class LoginController {

    private final UserService userService;

    private final TokenService tokenService;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "根据用户名和密码进行登录验证")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "登录成功"),
            @ApiResponse(responseCode = "401", description = "用户名或密码错误")
    })
    public Result login(
            @Parameter(description = "用户名", required = true) @RequestParam String username,
            @Parameter(description = "密码", required = true) @RequestParam String password) {
        // 参数验证逻辑
        if (username == null || username.trim().isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "用户名不能为空");
        }
        if (username.length() < 5 || username.length() > 20) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "用户名长度必须在5-20个字符之间");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "密码不能为空");
        }
        if (password.length() < 5 || password.length() > 20) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "密码长度必须在5-20个字符之间");
        }

        UserDto userDto = userService.validateUser(username, password);
        
        if (userDto != null) {
            String token = tokenService.createToken(userDto);
            return Result.success("登录成功！",token);
        } else {
            return Result.error(401, "用户名或密码错误");
        }
    }
}
