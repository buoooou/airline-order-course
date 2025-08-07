package com.postion.airlineorderbackend.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.postion.airlineorderbackend.dto.LoginDto;
import com.postion.airlineorderbackend.exception.InvalidCredentialsException;
import com.postion.airlineorderbackend.exception.ResourceNotFoundException;
import com.postion.airlineorderbackend.model.User;
import com.postion.airlineorderbackend.repo.UserRepository;
import com.postion.airlineorderbackend.service.AuthService;
import com.postion.airlineorderbackend.service.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	
	@Override
	public String login(LoginDto loginDto) {
		User user = userRepository.findByUsername(loginDto.getUsername())
				.orElseThrow(() -> new ResourceNotFoundException("未找到指定用戶。用戶名：" + loginDto.getUsername()));
		if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
			throw new InvalidCredentialsException("Invalid credentials");
		}
		String token = jwtService.generateToken(user.getUsername());
		return token;
	}
}
