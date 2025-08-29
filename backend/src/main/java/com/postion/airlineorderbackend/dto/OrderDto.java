package com.postion.airlineorderbackend.dto;

import com.postion.airlineorderbackend.model.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class OrderDto {
    private Long id;
    private String orderNumber;
    private OrderStatus status;
    private BigDecimal amount;
    private LocalDateTime creationDate;
    private UserDto user;
    private Map<String, Object> flightInfo; // 用于聚合模拟航班信息
    
    // ====== 构造函数 ======
    public OrderDto() {
    }

    public OrderDto(Long id, String orderNumber, OrderStatus status, BigDecimal amount,
                    LocalDateTime creationDate, UserDto user, Map<String, Object> flightInfo) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.status = status;
        this.amount = amount;
        this.creationDate = creationDate;
        this.user = user;
        this.flightInfo = flightInfo;
    }

    // ====== Getter 和 Setter ======
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public Map<String, Object> getFlightInfo() {
        return flightInfo;
    }

    public void setFlightInfo(Map<String, Object> flightInfo) {
        this.flightInfo = flightInfo;
    }

    // ====== toString ======
    @Override
    public String toString() {
        return "OrderDto{" +
                "id=" + id +
                ", orderNumber='" + orderNumber + '\'' +
                ", status=" + status +
                ", amount=" + amount +
                ", creationDate=" + creationDate +
                ", user=" + user +
                ", flightInfo=" + flightInfo +
                '}';
    }

    @Data
    public static class UserDto {
        private Long id;
        private String username;
        
        public UserDto() {
        }

        public UserDto(Long id, String username) {
            this.id = id;
            this.username = username;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        @Override
        public String toString() {
            return "UserDto{" +
                    "id=" + id +
                    ", username='" + username + '\'' +
                    '}';
        }
    }
}
