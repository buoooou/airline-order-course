package com.postion.airlineorderbackend.dto;

import lombok.Data;

@Data
public class AirportDto {
    private String iataCode;
    private String name;
    private String city;
    private String country;
}