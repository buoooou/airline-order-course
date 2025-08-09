package com.postion.airlineorderbackend.service;

import java.util.List;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.exception.DataNotFoundException;
import com.postion.airlineorderbackend.exception.InvalidOrderStatusException;
import com.postion.airlineorderbackend.exception.UserNotFoundException;
import com.postion.airlineorderbackend.model.OrderStatus;

public interface OrderService {

        /**
         * Get all orders.
         * 
         * @return List of all orders.
         */
        List<OrderDto> getAllOrders();

        /**
         * Get order by order id.
         * 
         * @param id Order id.
         * @return Found order.
         * @throws DataNotFoundException
         */
        OrderDto getOrderById(Long id) throws DataNotFoundException;

        /**
         * Pay order.
         * 
         * @param orderid
         * @param userid
         * @return
         * @throws DataNotFoundException
         * @throws UserNotFoundException
         * @throws InvalidOrderStatusException
         */
        OrderDto payOrder(Long orderid, Long userid)
                        throws DataNotFoundException, UserNotFoundException, InvalidOrderStatusException;

        /**
         * Cancel order.
         * 
         * @param orderid
         * @param userid
         * @return
         * @throws DataNotFoundException
         * @throws UserNotFoundException
         * @throws InvalidOrderStatusException
         */
        OrderDto cancelOrder(Long orderid, Long userid)
                        throws DataNotFoundException, UserNotFoundException, InvalidOrderStatusException;

        /**
         * Retry order.
         * 
         * @param orderid
         * @param userid
         * @return
         * @throws DataNotFoundException
         * @throws UserNotFoundException
         * @throws InvalidOrderStatusException
         */
        OrderDto retryOrder(Long orderid, Long userid)
                        throws DataNotFoundException, UserNotFoundException, InvalidOrderStatusException;

        /**
         * Request ticket issuance.
         */
        void requestTicketIssuance();

        /**
         * Verify ticket issuance.
         */
        void verifyTicketIssuance();

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
