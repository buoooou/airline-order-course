package com.postion.airlineorderbackend.dto;

import lombok.Data;

@Data
public class AirlineDto {
    private String icaoCode;
    private String name;
    private String logoUrl;
}