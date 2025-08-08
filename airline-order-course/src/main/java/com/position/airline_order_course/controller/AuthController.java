package com.position.airline_order_course.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.position.airline_order_course.dto.JwtResponse;
import com.position.airline_order_course.dto.LoginRequestDto;
import com.position.airline_order_course.util.JwtUtil;

/*
 * 登录认证Controller
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            if (!(authentication.getPrincipal() instanceof UserDetails userDetails)) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("用户信息不合法");
            }

            String token = jwtUtil.generateToken(userDetails);
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            JwtResponse response = new JwtResponse(token, userDetails.getUsername(), roles);
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            logger.warn("凭证错误", request.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户名或密码错误");
        } catch (LockedException e) {
            logger.warn("账户被锁定", request.getUsername());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("账户已被锁定");
        } catch (AccountExpiredException e) {
            logger.warn("账户已过期", request.getUsername());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("账户已过期");
        } catch (Exception e) {
            logger.error("服务器内部错误", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("服务器内部错误");
        }
    }
}