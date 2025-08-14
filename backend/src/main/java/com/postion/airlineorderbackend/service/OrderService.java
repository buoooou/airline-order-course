package com.postion.airlineorderbackend.service;

import java.util.List;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.exception.BusinessException;

public interface OrderService {
	
	/**
	 * 查询所有订单
	 * 
	 * @return 订单信息列表
	 */
    List<OrderDto> getAllOrders();

	/**
	 * 查询指定订单
	 * 
	 * @param id        订单ID
	 * @return 订单信息
	 * @throws BusinessException    未找到所选订单
	 */
    OrderDto getOrderById(Long id);

	/**
	 * 支付订单请求
	 * 
	 * @param id        订单ID
	 * @return 订单信息
	 */
    OrderDto payOrder(Long id);

	/**
	 * 出票处理
	 * 
	 * @param id        订单ID
	 * @throws BusinessException    线程被中断
	 */   
    void requestTicketIssuance(Long id);

	/**
	 * 取消订单请求
	 * 
	 * @param id        订单ID
	 * @return 订单信息
	 */
    OrderDto cancelOrder(Long id);
}
