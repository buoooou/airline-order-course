package com.postion.airlineorderbackend.dto;

import lombok.Data;

@Data
public class PassengerDto {
    private Long id;
    private Long userId;
    private String name;
    private String idType;
    private String idNumber;
    private String phone;
    private String email;
}