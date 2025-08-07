package com.postion.airlineorderbackend.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.jaxb.SpringDataJaxb.OrderDto;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.postion.airlineorderbackend.component.OrderMapper;
import com.postion.airlineorderbackend.dto.CommonResponseDto;
import com.postion.airlineorderbackend.dto.PostDummyTicketingRequestDto;
import com.postion.airlineorderbackend.dto.PostDummyTicketingResponseDto;
import com.postion.airlineorderbackend.entity.Order;
import com.postion.airlineorderbackend.entity.OrderStatus;
import com.postion.airlineorderbackend.exception.DataNotFoundException;
import com.postion.airlineorderbackend.exception.InvalidOrderStatusException;
import com.postion.airlineorderbackend.exception.UserNotFoundException;
import com.postion.airlineorderbackend.repository.OrderRepository;
import com.postion.airlineorderbackend.repository.UserRepository;
import com.postion.airlineorderbackend.service.OrderService;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of the OrderService interface.
 * Handles CRUD operations and business logic for Orders.
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final UserRepository userRepository;

    private final OrderMapper orderMapper;

    @Resource
    private RestTemplate restTemplate;

    /**
     * Retrieves all orders in the system.
     *
     * @return List of OrderDto objects.
     */
    @Override
    public List<OrderDto> getAllOrders() {
        List<Order> allOrders = orderRepository.findAll();
        return orderMapper.list2dto(allOrders);
    }

    /**
     * Retrieves an order by ID.
     *
     * @param id Order ID.
     * @return OrderDto object.
     * @throws DataNotFoundException if order does not exist.
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public OrderDto getOrderById(Long id) throws DataNotFoundException {
        return orderRepository.findById(id)
            .map(orderMapper::order2dto)
            .orElseThrow(DataNotFoundException::new);
    }

    /**
     * Pays for an order if conditions are met.
     *
     * @param orderId Order ID.
     * @param userId User ID.
     * @return Updated OrderDto.
     * @throws DataNotFoundException if order does not exist.
     * @throws UserNotFoundException if user is not the order owner.
     * @throws InvalidOrderStatusException if order status is not PENDING_PAYMENT.
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public OrderDto payOrder(Long orderId, Long userId)
            throws DataNotFoundException, UserNotFoundException, InvalidOrderStatusException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(DataNotFoundException::new);

        if (!order.getUser().getId().equals(userId)) {
            throw new UserNotFoundException();
        }
        if (!OrderStatus.PENDING_PAYMENT.equals(order.getStatus())) {
            throw new InvalidOrderStatusException();
        }
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
        return orderMapper.order2dto(order);
    }

    /**
     * Cancels an order if conditions are met.
     *
     * @param orderId Order ID.
     * @param userId User ID.
     * @return Updated OrderDto.
     * @throws DataNotFoundException if order does not exist.
     * @throws UserNotFoundException if user is not the order owner.
     * @throws InvalidOrderStatusException if order status is not eligible for cancellation.
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public OrderDto cancelOrder(Long orderId, Long userId)
            throws DataNotFoundException, UserNotFoundException, InvalidOrderStatusException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(DataNotFoundException::new);

        if (!order.getUser().getId().equals(userId)) {
            throw new UserNotFoundException();
        }
        if (!(OrderStatus.PENDING_PAYMENT.equals(order.getStatus())
                || OrderStatus.PAID.equals(order.getStatus())
                || OrderStatus.TICKETING_FAILED.equals(order.getStatus()))) {
            throw new InvalidOrderStatusException();
        }
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        return orderMapper.order2dto(order);
    }

    /**
     * Retries ticketing for an order if previous attempt failed.
     *
     * @param orderId Order ID.
     * @param userId User ID.
     * @return Updated OrderDto.
     * @throws DataNotFoundException if order does not exist.
     * @throws UserNotFoundException if user is not the order owner.
     * @throws InvalidOrderStatusException if order status is not TICKETING_FAILED.
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public OrderDto retryOrder(Long orderId, Long userId)
            throws DataNotFoundException, UserNotFoundException, InvalidOrderStatusException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(DataNotFoundException::new);

        if (!order.getUser().getId().equals(userId)) {
            throw new UserNotFoundException();
        }
        if (!OrderStatus.TICKETING_FAILED.equals(order.getStatus())) {
            throw new InvalidOrderStatusException();
        }
        order.setStatus(OrderStatus.PAID); // Set to PAID for processing ticketing
        orderRepository.save(order);
        return orderMapper.order2dto(order);
    }

    /**
     * Updates the status of an order.
     *
     * @param id Order ID.
     * @param status New status.
     * @return Updated OrderDto.
     * @throws DataNotFoundException if order does not exist.
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public OrderDto updateOrderStatus(Long id, OrderStatus status) throws DataNotFoundException {
        Order order = orderRepository.findById(id)
                .orElseThrow(DataNotFoundException::new);

        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.order2dto(updatedOrder);
    }

    /**
     * Requests ticket issuance for all PAID orders.
     * Updates order status based on ticketing API response.
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public void requestTicketIssuance() {
        List<Order> orders = orderRepository.findByStatus(OrderStatus.PAID);
        if (orders.isEmpty()) {
            return;
        }

        PostDummyTicketingRequestDto requestDto = new PostDummyTicketingRequestDto();
        requestDto.setOrders(orderMapper.list2dto(orders));
        HttpEntity<PostDummyTicketingRequestDto> request = new HttpEntity<>(requestDto);

        @SuppressWarnings("unchecked")
        CommonResponseDto<List<Long>> res = (CommonResponseDto<List<Long>>) restTemplate
                .postForObject(
                        "http://127.0.0.1:8080/api/dummy/ticket",
                        request, CommonResponseDto.class);

        if (res == null || !res.isSuccess()) {
            return;
        }
        orders.forEach(order -> {
            if (res.getData().contains(order.getId())) {
                order.setStatus(OrderStatus.TICKETING_IN_PROGRESS);
                orderRepository.save(order);
            }
        });
    }

    /**
     * Verifies ticket issuance for all TICKETING_IN_PROGRESS orders.
     * Updates order status based on verification API response.
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public void verifyTicketIssuance() {
        List<Order> orders = orderRepository.findByStatus(OrderStatus.TICKETING_IN_PROGRESS);
        if (orders.isEmpty()) {
            return;
        }

        PostDummyTicketingRequestDto requestDto = new PostDummyTicketingRequestDto();
        requestDto.setOrders(orderMapper.list2dto(orders));
        HttpEntity<PostDummyTicketingRequestDto> request = new HttpEntity<>(requestDto);

        @SuppressWarnings("unchecked")
        CommonResponseDto<PostDummyTicketingResponseDto> res = (CommonResponseDto<PostDummyTicketingResponseDto>) restTemplate
                .postForObject(
                        "http://127.0.0.1:8080/api/dummy/verify",
                        request, CommonResponseDto.class);

        if (res == null || !res.isSuccess() || res.getData() == null) {
            return;
        }
        orders.forEach(order -> {
            if (res.getData().getTicketedOrderIDs().contains(order.getId())) {
                order.setStatus(OrderStatus.TICKETED);
                orderRepository.save(order);
            } else if (res.getData().getFailedOrderIDs().contains(order.getId())) {
                order.setStatus(OrderStatus.TICKETING_FAILED);
                orderRepository.save(order);
            }
        });
    }

    /**
     * Cancels orders that have been pending payment for more than 30 minutes.
     * Updates their status to CANCELLED.
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public void cancelPaymentExpiredTickets() {
        LocalDateTime timeExpire = LocalDateTime.now().minusMinutes(30);
        List<Order> orders = orderRepository.findByStatusAndCreationDateBefore(OrderStatus.PENDING_PAYMENT, timeExpire);

        if (orders.isEmpty()) {
            return;
        }

        LocalDateTime timeNow = LocalDateTime.now();
        for (Order order : orders) {
            order.setStatus(OrderStatus.CANCELLED);

            System.out.println(order.getStatus());
        }
        orderRepository.saveAll(orders);
    }

    /**
     * Retrieves all orders made by a specific user.
     *
     * @param id User ID.
     * @return List of OrderDto objects.
     */
    @Override
    public List<OrderDto> getAllOrdersByUserId(Long id) {
        List<Order> result = userRepository.findOrdersByUserId(id);
        return orderMapper.list2dto(result);
    }

	@Override
	public Object createOrder(String email, Long flightId) {
		// TODO Auto-generated method stub
		return null;
	}
}