package com.postion.airlineorderbackend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.postion.airlineorderbackend.dto.UserDto;
import com.postion.airlineorderbackend.dto.request.LoginRequestDto;
import com.postion.airlineorderbackend.dto.response.CommonResponseDto;
import com.postion.airlineorderbackend.dto.response.LoginResponseDto;
import com.postion.airlineorderbackend.exception.DataNotFoundException;
import com.postion.airlineorderbackend.service.UserService;
import com.postion.airlineorderbackend.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/login")
public class LoginController {

  private final UserService userService;

  private final JwtUtil jwtUtil;

  /**
   * Login.
   * 
   * @param requestData
   * @return
   */
  @CrossOrigin(origins = "*")
  @PostMapping
  public CommonResponseDto<LoginResponseDto> postLogin(@RequestBody LoginRequestDto requestData) {
    CommonResponseDto<LoginResponseDto> response = new CommonResponseDto<LoginResponseDto>(false,
        200,
        "账户或密码错误，登陆失败。",
        null);
    if (!StringUtils.hasText(requestData.getUsername()) || !StringUtils.hasText(requestData.getPassword())) {
      return response;
    }

    UserDto userDto = null;
    try {
      userDto = userService.findByUsernameAndPassword(requestData.getUsername(), requestData.getPassword());
    } catch (DataNotFoundException e) {
      return response;
    }
    String token = jwtUtil.genToken(userDto);
    LoginResponseDto responseDto = LoginResponseDto.builder().id(userDto.getUserid()).username(userDto.getUsername())
        .role(userDto.getRole()).token(token).build();
    return new CommonResponseDto<LoginResponseDto>(true,
        200,
        "",
        responseDto);
  }

}
