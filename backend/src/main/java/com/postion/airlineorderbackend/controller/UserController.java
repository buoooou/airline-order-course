package com.postion.airlineorderbackend.controller;

import com.postion.airlineorderbackend.model.User;
import com.postion.airlineorderbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import javax.validation.Valid;
import java.util.List;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "用户管理", description = "用户相关的API接口")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取用户信息",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "401", description = "未授权"),
        @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser() {
        User currentUser = userService.getCurrentUser();
        return ResponseEntity.ok(currentUser);
    }

    @Operation(summary = "根据ID获取用户信息", description = "根据用户ID获取用户详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取用户信息",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "404", description = "用户不存在"),
        @ApiResponse(responseCode = "401", description = "未授权")
    })
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(
            @Parameter(description = "用户ID", required = true) @PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "根据用户名获取用户信息", description = "根据用户名获取用户详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取用户信息",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "404", description = "用户不存在"),
        @ApiResponse(responseCode = "401", description = "未授权")
    })
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(
            @Parameter(description = "用户名", required = true) @PathVariable String username) {
        User user = userService.findByUsername(username);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "创建新用户", description = "创建新的用户账户")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "用户创建成功",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "409", description = "用户名已存在")
    })
    @PostMapping
    public ResponseEntity<User> createUser(
            @Parameter(description = "用户信息", required = true) @Valid @RequestBody User user) {
        User createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @Operation(summary = "更新用户信息", description = "更新指定用户的信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "更新成功",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "404", description = "用户不存在"),
        @ApiResponse(responseCode = "401", description = "未授权")
    })
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @Parameter(description = "用户ID", required = true) @PathVariable Long id,
            @Parameter(description = "用户更新信息", required = true) @Valid @RequestBody User user) {
        user.setId(id);
        User updatedUser = userService.updateUser(user);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "删除用户", description = "删除指定的用户（仅管理员权限）")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "删除成功"),
        @ApiResponse(responseCode = "404", description = "用户不存在"),
        @ApiResponse(responseCode = "401", description = "未授权"),
        @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "用户ID", required = true) @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
} 