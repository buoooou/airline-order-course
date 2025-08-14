package com.airline.dto;

import com.airline.entity.OrderItem;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderItemDto {

    private Long id;

    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @NotNull(message = "航班ID不能为空")
    private Long flightId;

    private FlightDto flight;

    @NotNull(message = "旅客ID不能为空")
    private Long passengerId;

    private PassengerDto passenger;

    @NotNull(message = "座位等级不能为空")
    private OrderItem.SeatClass seatClass;

    @Size(max = 10, message = "座位号长度不能超过10个字符")
    private String seatNumber;

    @NotNull(message = "票价不能为空")
    @Positive(message = "票价必须大于0")
    private BigDecimal ticketPrice;

    private BigDecimal taxesFees = BigDecimal.ZERO;

    @NotNull(message = "总价不能为空")
    @Positive(message = "总价必须大于0")
    private BigDecimal totalPrice;

    private OrderItem.TicketStatus ticketStatus;

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

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public FlightDto getFlight() {
        return flight;
    }

    public void setFlight(FlightDto flight) {
        this.flight = flight;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    public PassengerDto getPassenger() {
        return passenger;
    }

    public void setPassenger(PassengerDto passenger) {
        this.passenger = passenger;
    }

    public OrderItem.SeatClass getSeatClass() {
        return seatClass;
    }

    public void setSeatClass(OrderItem.SeatClass seatClass) {
        this.seatClass = seatClass;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public BigDecimal getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(BigDecimal ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public BigDecimal getTaxesFees() {
        return taxesFees;
    }

    public void setTaxesFees(BigDecimal taxesFees) {
        this.taxesFees = taxesFees;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public OrderItem.TicketStatus getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(OrderItem.TicketStatus ticketStatus) {
        this.ticketStatus = ticketStatus;
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