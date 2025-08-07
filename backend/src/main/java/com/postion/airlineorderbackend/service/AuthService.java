package com.postion.airlineorderbackend.service;

import com.postion.airlineorderbackend.dto.LoginDto;

public interface AuthService {
	String login(LoginDto loginDto);
}
