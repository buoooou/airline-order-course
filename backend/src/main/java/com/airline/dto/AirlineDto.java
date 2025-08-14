package com.airline.dto;

import com.airline.entity.Airline;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AirlineDto {

    private Long id;

    @NotBlank(message = "航空公司代码不能为空")
    @Size(max = 10, message = "航空公司代码长度不能超过10个字符")
    private String code;

    @NotBlank(message = "航空公司名称不能为空")
    @Size(max = 100, message = "航空公司名称长度不能超过100个字符")
    private String name;

    @Size(max = 255, message = "logo URL长度不能超过255个字符")
    private String logoUrl;

    @Size(max = 50, message = "国家名称长度不能超过50个字符")
    private String country;

    private Airline.Status status;

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

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Airline.Status getStatus() {
        return status;
    }

    public void setStatus(Airline.Status status) {
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