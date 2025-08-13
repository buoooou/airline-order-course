package com.postion.airlineorderbackend.controller;

import com.postion.airlineorderbackend.entity.Result;
import com.postion.airlineorderbackend.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@Tag(name = "用户管理", description = "用户相关操作接口")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final IUserService userService;

    // 查询所有用户
    @Operation(summary = "获取所有用户", description = "返回系统中所有的用户信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "成功获取用户列表"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping
    public Result getAllUsers() {
        return Result.success(userService.getAllUsers());
    }

    // 根据用户名查询用户
    @Operation(summary = "根据用户名查询用户", description = "根据用户名返回用户信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "成功获取用户信息"),
        @ApiResponse(responseCode = "404", description = "用户不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/getUserByUsername")
    public Result getUserByUsername(
            @Parameter(description = "用户名", required = true) @RequestParam String username) {
        return Result.success(userService.getUserByUsername(username));
    }

    // 根据用户Id查询用户
    @Operation(summary = "根据ID查询用户", description = "根据用户ID返回用户信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "成功获取用户信息"),
        @ApiResponse(responseCode = "404", description = "用户不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/{id}")
    public Result getUserById(
            @Parameter(description = "用户ID", required = true) @PathVariable Long id) {
        return Result.success(userService.getUserById(id));
    }
}
