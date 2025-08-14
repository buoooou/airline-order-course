package com.airline.dto;

import com.airline.entity.Flight;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FlightDto {

    private Long id;

    @NotBlank(message = "航班号不能为空")
    @Size(max = 20, message = "航班号长度不能超过20个字符")
    private String flightNumber;

    @NotNull(message = "航空公司不能为空")
    private Long airlineId;

    private AirlineDto airline;

    @NotNull(message = "出发机场不能为空")
    private Long departureAirportId;

    private AirportDto departureAirport;

    @NotNull(message = "到达机场不能为空")
    private Long arrivalAirportId;

    private AirportDto arrivalAirport;

    @NotNull(message = "出发时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime departureTime;

    @NotNull(message = "到达时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime arrivalTime;

    @Size(max = 50, message = "机型长度不能超过50个字符")
    private String aircraftType;

    @NotNull(message = "总座位数不能为空")
    @Positive(message = "总座位数必须大于0")
    private Integer totalSeats;

    private Integer availableSeats;

    private BigDecimal economyPrice;

    private BigDecimal businessPrice;

    private BigDecimal firstPrice;

    private Flight.Status status;

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

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public Long getAirlineId() {
        return airlineId;
    }

    public void setAirlineId(Long airlineId) {
        this.airlineId = airlineId;
    }

    public AirlineDto getAirline() {
        return airline;
    }

    public void setAirline(AirlineDto airline) {
        this.airline = airline;
    }

    public Long getDepartureAirportId() {
        return departureAirportId;
    }

    public void setDepartureAirportId(Long departureAirportId) {
        this.departureAirportId = departureAirportId;
    }

    public AirportDto getDepartureAirport() {
        return departureAirport;
    }

    public void setDepartureAirport(AirportDto departureAirport) {
        this.departureAirport = departureAirport;
    }

    public Long getArrivalAirportId() {
        return arrivalAirportId;
    }

    public void setArrivalAirportId(Long arrivalAirportId) {
        this.arrivalAirportId = arrivalAirportId;
    }

    public AirportDto getArrivalAirport() {
        return arrivalAirport;
    }

    public void setArrivalAirport(AirportDto arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getAircraftType() {
        return aircraftType;
    }

    public void setAircraftType(String aircraftType) {
        this.aircraftType = aircraftType;
    }

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }

    public Integer getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }

    public BigDecimal getEconomyPrice() {
        return economyPrice;
    }

    public void setEconomyPrice(BigDecimal economyPrice) {
        this.economyPrice = economyPrice;
    }

    public BigDecimal getBusinessPrice() {
        return businessPrice;
    }

    public void setBusinessPrice(BigDecimal businessPrice) {
        this.businessPrice = businessPrice;
    }

    public BigDecimal getFirstPrice() {
        return firstPrice;
    }

    public void setFirstPrice(BigDecimal firstPrice) {
        this.firstPrice = firstPrice;
    }

    public Flight.Status getStatus() {
        return status;
    }

    public void setStatus(Flight.Status status) {
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