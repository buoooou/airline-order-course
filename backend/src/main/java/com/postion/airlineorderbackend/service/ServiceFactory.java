package com.postion.airlineorderbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 服务工厂类
 * 用于管理不同的服务实现，便于依赖注入和测试
 */
@Component
public class ServiceFactory {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 获取订单服务
     * @return 订单服务实例
     */
    public OrderService getOrderService() {
        return orderService;
    }
    
    /**
     * 获取用户服务
     * @return 用户服务实例
     */
    public UserService getUserService() {
        return userService;
    }
} 