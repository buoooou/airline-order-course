package com.postion.airlineorderbackend.service;

import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.OrderStatus;
import com.postion.airlineorderbackend.model.User;

import java.util.List;

/**
 * 订单服务接口
 * 定义订单相关的业务操作
 */
public interface OrderService {
    
    /**
     * 获取所有订单（管理员）
     * @return 所有订单列表
     */
    List<Order> getAllOrders();
    
    /**
     * 获取当前用户的订单
     * @param currentUser 当前用户
     * @return 用户订单列表
     */
    List<Order> getCurrentUserOrders(User currentUser);
    
    /**
     * 根据ID获取订单详情
     * @param id 订单ID
     * @return 订单详情
     * @throws RuntimeException 当订单不存在时抛出异常
     */
    Order getOrderById(Long id);
    
    /**
     * 创建新订单
     * @param order 订单信息
     * @return 创建的订单
     */
    Order createOrder(Order order);
    
    /**
     * 支付订单
     * @param id 订单ID
     * @return 更新后的订单
     * @throws RuntimeException 当订单状态不允许支付时抛出异常
     */
    Order payOrder(Long id);
    
    /**
     * 取消订单
     * @param id 订单ID
     * @return 更新后的订单
     * @throws RuntimeException 当订单状态不允许取消时抛出异常
     */
    Order cancelOrder(Long id);
    
    /**
     * 重试出票（针对出票失败的订单）
     * @param id 订单ID
     * @return 更新后的订单
     * @throws RuntimeException 当订单状态不允许重试出票时抛出异常
     */
    Order retryTicketing(Long id);
    
    /**
     * 按状态筛选订单
     * @param status 订单状态
     * @return 符合条件的订单列表
     */
    List<Order> getOrdersByStatus(OrderStatus status);
    
    /**
     * 更新订单
     * @param order 订单信息
     * @return 更新后的订单
     */
    Order updateOrder(Order order);
    
    /**
     * 删除订单（仅管理员）
     * @param id 订单ID
     */
    void deleteOrder(Long id);
}