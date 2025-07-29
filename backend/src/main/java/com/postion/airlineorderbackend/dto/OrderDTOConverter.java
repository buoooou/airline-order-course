package com.postion.airlineorderbackend.dto;

import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderDTOConverter {
    
    /**
     * 将Order实体转换为OrderResponseDTO
     */
    public OrderResponseDTO toResponseDTO(Order order) {
        if (order == null) {
            return null;
        }
        
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setStatus(order.getStatus());
        dto.setAmount(order.getAmount());
        dto.setCreationDate(order.getCreationDate());
        
        // 转换用户信息
        if (order.getUser() != null) {
            OrderResponseDTO.UserInfoDTO userInfo = new OrderResponseDTO.UserInfoDTO();
            userInfo.setId(order.getUser().getId());
            userInfo.setUsername(order.getUser().getUsername());
            userInfo.setRole(order.getUser().getRole());
            dto.setUser(userInfo);
        }
        
        return dto;
    }
    
    /**
     * 将Order实体列表转换为OrderResponseDTO列表
     */
    public List<OrderResponseDTO> toResponseDTOList(List<Order> orders) {
        if (orders == null) {
            return null;
        }
        
        return orders.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 将OrderCreateDTO转换为Order实体
     */
    public Order toEntity(OrderCreateDTO dto, User user) {
        if (dto == null) {
            return null;
        }
        
        Order order = new Order();
        order.setAmount(dto.getAmount());
        order.setUser(user);
        order.setStatus(com.postion.airlineorderbackend.model.OrderStatus.PENDING_PAYMENT);
        // 其他字段如orderNumber和creationDate在Service层设置
        
        return order;
    }
    
    /**
     * 更新Order实体（用于部分更新）
     */
    public void updateEntityFromDTO(Order order, OrderUpdateDTO dto) {
        if (order == null || dto == null) {
            return;
        }
        
        if (dto.getStatus() != null) {
            order.setStatus(dto.getStatus());
        }
        
        // 可以根据需要添加其他字段的更新逻辑
    }
} 