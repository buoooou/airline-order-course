package com.postion.airlineorderbackend.service.impl;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.exception.BusinessException;
import com.postion.airlineorderbackend.mapper.OrderMapper;
import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.OrderStatus;
import com.postion.airlineorderbackend.repo.OrderRepository;
import com.postion.airlineorderbackend.service.OrderService;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    /**
     * getAllOrders
     *
     */
    @Override
    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * getOrderById
     *
     * @param id
     */
    @Override
    public OrderDto getOrderById(Long id) {
        log.info("Start getOrderById(), orderId:{}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> BusinessException.orderNotFound());

        OrderDto orderDto = orderMapper.toDto(order);

        log.info("End getOrderById(), orderId:{}", id);
        return orderDto;
    }

    /**
     * payOrder
     *
     * @param id
     */
    @Override
    @Transactional
    public OrderDto payOrder(Long id) {
        log.info("Start payOrder(), orderId:{}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> BusinessException.orderNotFound());

        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            log.info("End payOrder() failure, orderId:{} is not PENDING_PAYMENT, current status is{}", id,
                    order.getStatus());
            throw BusinessException.paymentFailed();
        }

        order.setStatus(OrderStatus.PAID);
        Order savedOrder = orderRepository.save(order);

        log.info("End payOrder(), orderId:{} status is updated to PAID", id);
        requestTicketIssuance(id);
        return orderMapper.toDto(savedOrder);
    }

    /**
     * requestTicketIssuance
     *
     * @param id
     */
    @Override
    public void requestTicketIssuance(Long id) {
        log.info("Start requestTicketIssuance(), orderId:{}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->  BusinessException.orderNotFound());

        if (order.getStatus() != OrderStatus.PAID) {
            throw BusinessException.notPaidStatus();
        }

        order.setStatus(OrderStatus.TICKETED);
        orderRepository.save(order);

        log.info("End requestTicketIssuance(), orderId:{}", id);

    }

    /**
     * cancelOrder
     *
     * @param id
     */
    @Override
    @Transactional
    public OrderDto cancelOrder(Long id) {
        log.info("Start cancelOrder(), orderId:{}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->  BusinessException.orderNotFound());

        if (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.TICKETED ) {
            throw BusinessException.orderAlreadyCancelled();
        }

        order.setStatus(OrderStatus.CANCELLED);
        Order savedOrder = orderRepository.save(order);

        log.info("End cancelOrder(), orderId:{}", id);

        return orderMapper.toDto(savedOrder);
    }

    /**
     * updateStatus
     *
     * @param id
     * @param newStatus
     */
    @Transactional
    public OrderDto updateStatus(Long id, OrderStatus newStatus) {

        log.info("Start updateStatus(), orderId: {}, newStatus: {}", id, newStatus);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> BusinessException.orderNotFound());

        OrderStatus currentStatus = order.getStatus();

        // Validate status transition
        if (!isValidStatusTransition(currentStatus, newStatus)) {
            log.info("Invalid status transition from: {}, to newStatus: {} for order{}", currentStatus, newStatus, id);
            throw BusinessException.invalidStatus();
        }

        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);

        log.info("End updateStatus(), orderId: {}, newStatus: {}", id, newStatus);
        return orderMapper.toDto(updatedOrder);
    }

    /**
     * cancelUnpaidOrders
     *
     */
    @Scheduled(fixedRate = 60000) // 每分钟执行一次
    @Transactional
    @SchedulerLock(name = "cancelUnpaidOrdersTask", // 每个任务的唯一标识
            lockAtMostFor = "55s", // 锁最多持有55秒
            lockAtLeastFor = "10s" // 锁至少持有10秒
    )
    public void cancelUnpaidOrders() {
        log.info("【Scheduled Task】Start cancelUnpaidOrders().");
        LocalDateTime fifteenMinutesAgo = LocalDateTime.now().minusMinutes(15);

        List<Order> unpaidOrders = orderRepository.findByStatusAndCreationDateBefore(
                OrderStatus.PENDING_PAYMENT,
                fifteenMinutesAgo);

        if (!unpaidOrders.isEmpty()) {
            log.info("【Scheduled Task】Found {} unpaid orders，Start to Cancel", unpaidOrders.size());

            for (Order order : unpaidOrders) {
                order.setStatus(OrderStatus.CANCELLED);
                log.debug(" - Order {} (Created On {}) Status is updated to CANCELLED",
                        order.getId(), order.getCreationDate());
            }

            orderRepository.saveAll(unpaidOrders);
        } else {
            log.info("【Scheduled Task】End cancelUnpaidOrders(). No upaidOrders. Time:{}", LocalDateTime.now());
        }
    }

    private boolean isValidStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        switch (currentStatus) {
            case PENDING_PAYMENT:
                return newStatus == OrderStatus.PAID ||
                        newStatus == OrderStatus.CANCELLED;

            case PAID:
                return newStatus == OrderStatus.TICKETING_IN_PROGRESS ||
                        newStatus == OrderStatus.CANCELLED;

            case TICKETING_IN_PROGRESS:
                return newStatus == OrderStatus.TICKETED ||
                        newStatus == OrderStatus.TICKETING_FAILED;

            case TICKETING_FAILED:
                return newStatus == OrderStatus.TICKETING_IN_PROGRESS ||
                        newStatus == OrderStatus.CANCELLED;

            case TICKETED:
                // Once ticketed, only cancellation is allowed
                return newStatus == OrderStatus.CANCELLED;

            case CANCELLED:
                // Cancelled orders cannot change status
                return false;

            default:
                throw new BusinessException(HttpStatus.BAD_REQUEST, "Unknown order status: " + currentStatus);
        }
    }

}