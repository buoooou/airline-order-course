package com.postion.airlineorderbackend.service;
import com.postion.airlineorderbackend.dto.OrderDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrderService {

    /**
     * 获取所有订单的列表，转换为OrderDto对象。
     * @return 所有订单的列表
     */
    List<OrderDto> getAllOrders();

    /**
     * 根据用户ID获取单个订单，并附加航班信息。
     * @param userId 用户ID
     * @return 对应的OrderDto对象，包含航班信息
     */
    List<OrderDto> getAllOrdersByUserId(Long userId);

    /**
     * 根据订单ID获取单个订单，并附加航班信息。
     * @param id 订单的唯一ID
     * @return 对应的OrderDto对象，包含航班信息
     */
    OrderDto getOrderById(Long id);

    /**
     * 支付订单。
     * @param id 订单的唯一ID
     * @return 支付后的OrderDto对象
     */
    OrderDto payOrder(Long id);

    /**
     * 异步触发方法，请求出票。
     * @param id 订单的唯一ID
     */
    void requestTicketIssuance(Long id);

    /**
     * 取消订单。
     * @param id 订单的唯一ID
     * @return 取消后的OrderDto对象
     */
    OrderDto cancelOrder(Long id);

    /**
     * 批量取消超时订单
     */
    void cancelUnpaidOrder();
}
