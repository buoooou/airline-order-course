package com.postion.airlineorderbackend.controller;

import com.postion.airlineorderbackend.config.JwtUtil;
import com.postion.airlineorderbackend.dto.ApiResponse;
import com.postion.airlineorderbackend.dto.AuthResponse;
import com.postion.airlineorderbackend.dto.LoginRequest;
import com.postion.airlineorderbackend.dto.RegisterRequest;
import com.postion.airlineorderbackend.entity.AppUser;
import com.postion.airlineorderbackend.repository.AppUserRepository;
import com.postion.airlineorderbackend.common.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(Constants.ApiPath.AUTH_PREFIX)
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.username(),
                            loginRequest.password()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(Constants.ErrorMessage.INVALID_CREDENTIALS));
        }
        return ResponseEntity
                .ok(ApiResponse.success(Constants.SuccessMessage.LOGIN_SUCCESS,
                        createAuthResponse(loginRequest.username())));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        String username = registerRequest.username();
        if (appUserRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(Constants.ErrorMessage.USERNAME_EXISTS));
        }

        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(registerRequest.password()));
        user.setRole(registerRequest.role());

        AppUser savedUser = appUserRepository.save(user);

        return ResponseEntity.ok(ApiResponse.success(Constants.SuccessMessage.REGISTER_SUCCESS,
                createAuthResponse(savedUser.getUsername())));
    }

    private AuthResponse createAuthResponse(String username) {
        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        final String jwt = jwtUtil.generateToken(userDetails);

        return new AuthResponse(jwt, Constants.Auth.TOKEN_PREFIX.trim(), username);
    }
}