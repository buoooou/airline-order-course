package com.postion.airlineorderbackend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.postion.airlineorderbackend.dto.LoginDto;
import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;

	@PostMapping("/login")
	@Operation(summary = "登录请求", description = "根据用户名密码验证登录")
	@ApiResponses(value = {
	        @ApiResponse(responseCode = "200", description = "成功登录",
	                content = @Content(mediaType = "application/json",
	                        schema = @Schema(implementation = OrderDto.class))),
	        @ApiResponse(responseCode = "401", description = "未授权"),
	        @ApiResponse(responseCode = "403", description = "密码错误"),
	        @ApiResponse(responseCode = "404", description = "用户不存在")
	})
	public ResponseEntity<com.postion.airlineorderbackend.dto.ApiResponse<String>> login(@RequestBody LoginDto loginDto) {
		String token = authService.login(loginDto);
		return ResponseEntity.ok(com.postion.airlineorderbackend.dto.ApiResponse.success(token));
	}
}
