package com.airline.dto;

import com.airline.entity.Order;
import com.airline.entity.OrderItem;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public class OrderCreateDto {

    @Size(max = 100, message = "联系人姓名长度不能超过100个字符")
    private String contactName;

    @Size(max = 20, message = "联系人电话长度不能超过20个字符")
    private String contactPhone;

    @Size(max = 100, message = "联系人邮箱长度不能超过100个字符")
    private String contactEmail;

    private String notes;

    @NotEmpty(message = "订单项不能为空")
    @Valid
    private List<OrderItemCreateDto> orderItems;

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<OrderItemCreateDto> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemCreateDto> orderItems) {
        this.orderItems = orderItems;
    }

    public static class OrderItemCreateDto {
        @NotBlank(message = "航班ID不能为空")
        private Long flightId;

        @NotBlank(message = "旅客ID不能为空")
        private Long passengerId;

        @NotBlank(message = "座位等级不能为空")
        private OrderItem.SeatClass seatClass;

        public Long getFlightId() {
            return flightId;
        }

        public void setFlightId(Long flightId) {
            this.flightId = flightId;
        }

        public Long getPassengerId() {
            return passengerId;
        }

        public void setPassengerId(Long passengerId) {
            this.passengerId = passengerId;
        }

        public OrderItem.SeatClass getSeatClass() {
            return seatClass;
        }

        public void setSeatClass(OrderItem.SeatClass seatClass) {
            this.seatClass = seatClass;
        }
    }
}