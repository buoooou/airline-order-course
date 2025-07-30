package com.postion.airlineorderbackend.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.postion.airlineorderbackend.Exception.AirlineBusinessException;
import com.postion.airlineorderbackend.Exception.AirlineApiClientException;
import com.postion.airlineorderbackend.Exception.AirlineApiTimeoutException;
import com.postion.airlineorderbackend.adapter.outbound.AirlineApiClient;
import com.postion.airlineorderbackend.constants.Constants;
import com.postion.airlineorderbackend.dto.ApiResponseDTO;
import com.postion.airlineorderbackend.dto.OrderDTO;
import com.postion.airlineorderbackend.mapper.OrderMapper;
import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.OrderStatus;
import com.postion.airlineorderbackend.repository.OrderRepository;
import com.postion.airlineorderbackend.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Value("${app.order.payment.expiration}")
    private int paymentExpiration;

    private final OrderRepository orderRepository;
    private final AirlineApiClient client;
    private final OrderMapper orderMapper = OrderMapper.INSTANCE;

    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final int INITIAL_BACKOFF = 500;
    private static final int BACKOFF_MULTIPLIER = 2;

    @Transactional
    @Override
    public List<OrderDTO> getAllOrders(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream().map(x -> orderMapper.orderToOrderDTO(x)).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public OrderDTO getOrderByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .map(x -> orderMapper.orderToOrderDTO(x))
                .orElseThrow(() -> new AirlineBusinessException(HttpStatus.SERVICE_UNAVAILABLE.value(), Constants.MSG_ORDER_NOT_FOUND));
    }

    @Transactional
    @Override
    @Retryable(
        stateful = true, 
        value = {AirlineApiTimeoutException.class}, 
        maxAttempts = MAX_RETRY_ATTEMPTS, 
        backoff = @Backoff(delay = INITIAL_BACKOFF, multiplier = BACKOFF_MULTIPLIER)
    )
    public OrderDTO payOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AirlineBusinessException(HttpStatus.SERVICE_UNAVAILABLE.value(), Constants.MSG_ORDER_NOT_FOUND));
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new IllegalStateException(Constants.ORDER_STATUS_INCORRECT);
        }

        ApiResponseDTO<OrderStatus> response = invokeAirlineApiWithRetry(order.getOrderNumber(), order.getStatus());
        if (response.getCode() != 200) {
            handleAirlineApiError(order, response);
        }
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
        log.info("订单#{} 支付成功。", order.getOrderNumber());
        return orderMapper.orderToOrderDTO(order);
    }

    @Transactional
    @Override
    @Retryable(
        stateful = true, 
        value = {AirlineApiTimeoutException.class}, 
        maxAttempts = MAX_RETRY_ATTEMPTS, 
        backoff = @Backoff(delay = INITIAL_BACKOFF, multiplier = BACKOFF_MULTIPLIER)
    )
    public OrderDTO cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AirlineBusinessException(HttpStatus.SERVICE_UNAVAILABLE.value(), Constants.MSG_ORDER_NOT_FOUND));
        if (order.getStatus() == OrderStatus.TICKETED) {
            throw new IllegalStateException(Constants.ORDER_STATUS_INCORRECT);
        }

        ApiResponseDTO<OrderStatus> response = invokeAirlineApiWithRetry(order.getOrderNumber(), order.getStatus());
        if (response.getCode() != 200) {
            handleAirlineApiError(order, response);
        }
        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);
        log.info("订单#{} 已取消。", order.getOrderNumber());
        return orderMapper.orderToOrderDTO(order);
    }

    @Transactional
    @Override
    @Retryable(
        stateful = true, 
        value = {AirlineApiTimeoutException.class}, 
        maxAttempts = MAX_RETRY_ATTEMPTS, 
        backoff = @Backoff(delay = INITIAL_BACKOFF, multiplier = BACKOFF_MULTIPLIER)
    )
    public OrderDTO createOrder(OrderDTO orderDto) {
        Order order = Order.builder()
                .orderNumber("ALOD" + LocalDate.now().format(DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_YMD)) + String.format("%06d", ThreadLocalRandom.current().nextInt(1000000)))
                .amount(orderDto.getAmount())
                .status(OrderStatus.PENDING_PAYMENT)
                .build();

        ApiResponseDTO<OrderStatus> response = invokeAirlineApiWithRetry(order.getOrderNumber(), OrderStatus.NONE);
        if (response.getCode() != 200) {
            handleAirlineApiError(order, response);
        }

        orderRepository.save(order);
        log.info("订单#{} 已创建。", order.getOrderNumber());
        return orderMapper.orderToOrderDTO(order);
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    @SchedulerLock(name = "cancelUnpaidOrders", lockAtMostFor = "55s", lockAtLeastFor = "10s")
    @Retryable(
        stateful = true, 
        value = {AirlineApiTimeoutException.class}, 
        maxAttempts = MAX_RETRY_ATTEMPTS, 
        backoff = @Backoff(delay = INITIAL_BACKOFF, multiplier = BACKOFF_MULTIPLIER)
    )
    public void cancelUnpaidOrders() {
        LocalDateTime timeExpire = LocalDateTime.now().minusMinutes(paymentExpiration);
        List<Order> unpaidOrders = orderRepository.findByStatusAndCreateTimeBefore(OrderStatus.PENDING_PAYMENT, timeExpire);
        if (unpaidOrders.isEmpty()) {
            return;
        }
        LocalDateTime currentTime = LocalDateTime.now();
        unpaidOrders.forEach(order -> {
            ApiResponseDTO<OrderStatus> response = invokeAirlineApiWithRetry(order.getOrderNumber(), order.getStatus());
            if (response.getCode() != 200) {
                handleAirlineApiError(order, response);
            }
            order.setStatus(response.getData());
            order.setUpdateTime(currentTime);
            orderRepository.save(order);
            log.info("订单#{} 因支付超时已取消", order.getOrderNumber());
        });
    }

    private ApiResponseDTO<OrderStatus> invokeAirlineApiWithRetry(String orderNumber, OrderStatus status) {
        log.info("模拟调用航司API，订单ID: {}，当前订单状态：{}", orderNumber, status.toString());
        return client.communicate(orderNumber, status);
    }

    @Recover
    public void recoverAirlineFailure(AirlineApiClientException e) {
        log.error("重试超过次数，跳过订单处理。原因: {}", e.getMessage());
    }

    private void handleAirlineApiError(Order order, ApiResponseDTO<OrderStatus> response) {
        switch (response.getCode()) {
            case 503: 
                log.error("航司API无法处理请求: {}。当前订单ID：{}", response.getMessage(), order.getOrderNumber());
                break;
            case 401:
                log.error("航司API认证失败: {}。当前订单ID：{}", response.getMessage(), order.getOrderNumber());
                break;
            default:
                log.error("航司系统错误: {}-{}。当前订单ID：{}", response.getCode(), response.getMessage(), order.getOrderNumber());
        }
        throw new AirlineApiClientException(HttpStatus.valueOf(response.getCode()), "航司API异常: " + response.getMessage());
    }

    @Deprecated
    private OrderDTO mapToDTO(Order entity) {
        OrderDTO orderDto = new OrderDTO();
        orderDto.setOrderNumber(entity.getOrderNumber());
        orderDto.setStatus(entity.getStatus());
        orderDto.setAmount(entity.getAmount());
        orderDto.setCreateTime(entity.getCreateTime());
        orderDto.setUpdateTime(entity.getUpdateTime());
        return orderDto;
    }
}
