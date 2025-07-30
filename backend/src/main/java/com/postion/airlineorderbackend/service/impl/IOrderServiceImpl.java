package com.postion.airlineorderbackend.service.impl;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.entity.Order;
import com.postion.airlineorderbackend.repo.OrderRepository;
import com.postion.airlineorderbackend.service.IOrderService;
import com.postion.airlineorderbackend.util.ModelMapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IOrderServiceImpl implements IOrderService {

    private final OrderRepository orderRepository;

    /**
     * 获取所有订单的列表，转换为OrderDto对象。
     *
     * @return 所有订单的列表
     */
    @Override
    public List<OrderDto> getAllOrders() {
        List<Order> orderList = orderRepository.findAll();
        return ModelMapperUtil.mapList(orderList, OrderDto.class);
    }

    /**
     * 根据用户ID获取单个订单，并附加航班信息。
     *
     * @param userId 用户ID
     * @return 对应的OrderDto对象，包含航班信息
     */
    @Override
    public List<OrderDto> getAllOrdersByUserId(Long userId) {
        List<Order> orderList = orderRepository.findAllByUserId(userId);
        return ModelMapperUtil.mapList(orderList, OrderDto.class);
    }

    /**
     * 根据唯一标识符获取单个订单，并附加航班信息。
     *
     * @param id 订单的唯一ID
     * @return 对应的OrderDto对象，包含航班信息
     * @throws RuntimeException 如果找不到指定ID的订单
     */
    @Override
    public OrderDto getOrderById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));;
        return ModelMapperUtil.map(order, OrderDto.class);
    }

    /**
     * 支付订单。
     *
     * @param id 订单的唯一ID
     * @return 支付后的OrderDto对象
     */
    @Override
    public OrderDto payOrder(Long id) {
        return null;
    }

    /**
     * 异步触发方法，请求出票。
     *
     * @param id 订单的唯一ID
     */
    @Override
    public void requestTicketIssuance(Long id) {

    }

    /**
     * 取消订单。
     *
     * @param id 订单的唯一ID
     * @return 取消后的OrderDto对象
     */
    @Override
    public OrderDto cancelOrder(Long id) {
        return null;
    }
}
