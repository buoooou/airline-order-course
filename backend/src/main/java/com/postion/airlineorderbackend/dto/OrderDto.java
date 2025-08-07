package com.postion.airlineorderbackend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import com.postion.airlineorderbackend.model.OrderStatus;

/**
 * 订单数据传输对象
 * 用于订单相关API接口的数据传输
 * 
 * @author Postion
 * @version 1.0
 * @since 2024-01-01
 */
@Data
public class OrderDto {
    /**
     * 订单唯一标识
     */
    private Long id;
    
    /**
     * 订单号
     * 格式：ORDER-YYYYMMDD-XXXXXX
     */
    private String orderNumber;
    
    /**
     * 订单状态
     * 包括：待支付、已支付、出票中、出票失败、已出票、已取消
     */
    private OrderStatus status;
    
    /**
     * 订单金额
     * 精确到小数点后两位
     */
    private BigDecimal amount;
    
    /**
     * 订单创建时间
     */
    private LocalDateTime creationDate;
    
    /**
     * 关联的用户信息
     */
    private UserDto user;
    
    /**
     * 航班相关信息
     * 包含航班号、出发地、目的地、出发时间等
     */
    private Map<String, Object> flightInfo;
}