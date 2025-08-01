package com.airline.order.dto;

import java.util.List;

/**
 * 用户数据传输对象
 */
public class UserDTO {
    
    private Long id;
    private String username;
    private String role;
    private List<OrderDTO> orders;
    
    // 构造函数
    public UserDTO() {
    }
    
    public UserDTO(Long id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }
    
    // Getter和Setter方法
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
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public List<OrderDTO> getOrders() {
        return orders;
    }
    
    public void setOrders(List<OrderDTO> orders) {
        this.orders = orders;
    }
    
    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}