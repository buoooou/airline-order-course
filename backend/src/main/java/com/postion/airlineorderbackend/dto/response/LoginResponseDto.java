package com.postion.airlineorderbackend.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDto {

  private Long id;

  private String username;

  private String role;

  private String token;

}
