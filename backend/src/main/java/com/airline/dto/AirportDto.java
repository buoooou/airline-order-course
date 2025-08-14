package com.airline.dto;

import com.airline.entity.Airport;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AirportDto {

    private Long id;

    @NotBlank(message = "机场代码不能为空")
    @Size(max = 10, message = "机场代码长度不能超过10个字符")
    private String code;

    @NotBlank(message = "机场名称不能为空")
    @Size(max = 100, message = "机场名称长度不能超过100个字符")
    private String name;

    @NotBlank(message = "城市不能为空")
    @Size(max = 50, message = "城市名称长度不能超过50个字符")
    private String city;

    @NotBlank(message = "国家不能为空")
    @Size(max = 50, message = "国家名称长度不能超过50个字符")
    private String country;

    @Size(max = 50, message = "时区长度不能超过50个字符")
    private String timezone;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private Airport.Status status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public Airport.Status getStatus() {
        return status;
    }

    public void setStatus(Airport.Status status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}