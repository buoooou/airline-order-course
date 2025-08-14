package com.postion.airlineorderbackend.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import com.postion.airlineorderbackend.Exception.AirlineBusinessException;
import com.postion.airlineorderbackend.constants.Constants;
import com.postion.airlineorderbackend.dto.AuthResponseDTO;
import com.postion.airlineorderbackend.dto.UserDTO;
import com.postion.airlineorderbackend.mapper.UserMapper;
import com.postion.airlineorderbackend.repository.UserRepository;
import com.postion.airlineorderbackend.service.AuthService;
import com.postion.airlineorderbackend.service.UserService;
import com.postion.airlineorderbackend.util.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final UserMapper userMapper = UserMapper.INSTANCE;

    @Override
    public AuthResponseDTO authenticate(String username, String password) throws AuthenticationException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        UserDTO userDto = userRepository.findByUsername(username)
                .map(x -> userMapper.userToUserDTO(x))
                .orElseThrow(() -> new AirlineBusinessException(HttpStatus.SERVICE_UNAVAILABLE.value(), Constants.MSG_USER_NOT_FOUND));

        log.info("用户认证通过。用户ID：{}，用户名：{}", userDto.getId(), username);

        return AuthResponseDTO.builder().token(jwtUtil.generateToken(authentication.getName())).userId(userDto.getId()).build();
    }

}
