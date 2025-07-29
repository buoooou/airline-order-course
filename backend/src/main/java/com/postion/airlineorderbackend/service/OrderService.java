package com.postion.airlineorderbackend.service;

import java.util.List;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.model.OrderStatus;

public interface OrderService {

  /**
   * Get all orders.
   * 
   * @return List of all orders.
   */
  List<OrderDto> getAllOrders();

  OrderDto getOrderById(Long id);

  OrderDto payOrder(Long orderid, Long userid);

  OrderDto cancelOrder(Long orderid, Long userid);

  OrderDto retryOrder(Long orderid, Long userid);

  void requestTicketIssuance(Long id);

  /**
   * Update order to specified status.
   * 
   * @param id     The order id.
   * @param status The order status.
   * @return The order updated. Return null if not found.
   */
  OrderDto updateOrderStatus(Long id, OrderStatus status);

  /**
   * Cancel payment expired tickets.
   */
  void cancelPaymentExpiredTickets();

  /**
   * Get all orders of user with specified user id.
   * 
   * @param id The user id.
   * @return All orders of the user.
   */
  List<OrderDto> getAllOrdersByUserId(Long id);

}
