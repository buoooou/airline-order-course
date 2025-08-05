package com.postion.airlineorderbackend.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postion.airlineorderbackend.adapter.outbound.AirlineApiClient;
import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.enums.TicketStatusErrorCode;
import com.postion.airlineorderbackend.exception.TicketStatusException;
import com.postion.airlineorderbackend.mapper.OrderMapper;
import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.OrderStatus;
import com.postion.airlineorderbackend.repo.OrderRepository;
import com.postion.airlineorderbackend.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final AirlineApiClient airlineApiClient;

    public OrderDto getOrderById(Long id){
       Optional<Order> optOrder = orderRepository.findById(id);
        if(optOrder.isPresent()) {
            return OrderMapper.INSTANCE.orderToOrderDto(optOrder.get());
        }
        return null;
    }

    public List<OrderDto> getAllOrders(){
        List<Order> orderList = orderRepository.findAll();
        if(orderList.size()>0) {
            return new ArrayList<>();
        } else {
            return OrderMapper.INSTANCE.ordersToOrderDtos(orderList);
        }
    }

    /**
     * 处理订单支付请求。
     * 该方法用于处理订单的支付操作，包括状态校验、状态更新以及异步触发出票逻辑。
     * @param id 订单ID，不能为null。
     * @return 支付成功后的订单DTO对象；如果订单不存在，返回null。
     * @throws TicketStatusException 如果订单状态不是PENDING_PAYMENT，抛出此异常。
     * 逻辑说明：
     * 根据订单ID查询订单信息。
     * 校验订单状态是否为PENDING_PAYMENT，否则抛出异常。
     * 更新订单状态为PAID并保存。
     * 异步触发出票逻辑，记录成功或失败日志。 
     */
    @Transactional
    @SchedulerLock(name = "payOrderTask", lockAtLeastFor = "10s", lockAtMostFor = "55s")
    public OrderDto payOrder(Long id){
        log.info("开始处理支付订单请求,订单ID:{}", id);
        Optional<Order> optOrder = orderRepository.findById(id);
        if(optOrder.isPresent()) {
            Order order = optOrder.get();
            // 幂等性处理：如果订单已经是已支付状态，直接返回
            if (order.getStatus() == OrderStatus.PAID) {
                log.info("订单已支付，无需重复操作，订单ID:{}", id);
                return OrderMapper.INSTANCE.orderToOrderDto(order);
            }
            // 状态机校验：只有 PENDING_PAYMENT 状态的订单才能支付
            if(order.getStatus() != OrderStatus.PENDING_PAYMENT){
                throw new TicketStatusException(TicketStatusErrorCode.TICKET_ISSUE_FAILED, "非待支付状态的订单，不能进行支付，当前状态：" + order.getStatus().toString());
            }

            order.setStatus(OrderStatus.PAID);
            orderRepository.save(order);

            // 异步触发出票逻辑
            CompletableFuture.runAsync(() -> {
                try {
                    log.info("异步触发出票逻辑,订单ID:{}", id);
                    requestTicketIssuance(id);
                } catch (Exception e) {
                    log.error("异步出票失败,订单ID:{}, 错误信息:{}", id, e.getMessage(), e);
                    // 支付失败回调处理
                    order.setStatus(OrderStatus.PAYMENT_FAILED);
                    orderRepository.save(order);
                }
            });

            return OrderMapper.INSTANCE.orderToOrderDto(order);
        }
        return null;
    }


    @Transactional
    @SchedulerLock(name = "cancelOrder_#{#id}", lockAtLeastFor = "5s", lockAtMostFor = "30s")
    public OrderDto cancelOrder(Long id){
        log.info("开始处理订单取消请求,订单ID:{}", id);
        Optional<Order> optOrder = orderRepository.findById(id);
        if(optOrder.isPresent()) {
            Order order = optOrder.get();
             // 状态机校验：支持更多状态的取消逻辑
            if(order.getStatus() == OrderStatus.PENDING_PAYMENT || 
               order.getStatus() == OrderStatus.PAID ||
               order.getStatus() == OrderStatus.TICKETING_IN_PROGRESS ||
               order.getStatus() == OrderStatus.TICKETING_FAILED){
                
                order.setStatus(OrderStatus.CANCELLED);
                orderRepository.save(order);
                
                // 幂等性处理：确保订单状态不会重复取消
                if (order.getStatus() == OrderStatus.CANCELLED) {
                    log.info("订单已取消，无需重复操作，订单ID:{}", id);
                    return OrderMapper.INSTANCE.orderToOrderDto(order);
                }
                
                return OrderMapper.INSTANCE.orderToOrderDto(order);
            } else {
                throw new TicketStatusException(TicketStatusErrorCode.TICKET_ISSUE_FAILED, "当前状态的订单不能取消，当前状态：" + order.getStatus().toString());
            }
        }
        return new OrderDto();
    }


    @Scheduled(fixedRate = 600000)
    @Transactional
    @SchedulerLock(name = "cancelOrderTask", lockAtLeastFor = "10s", lockAtMostFor = "55s")
    public void cancelUnpaidOrders() {
        log.info("[定时任务]检查支付超时的订单。");
        LocalDateTime fifteenMinutesAgo = LocalDateTime.now().minusMinutes(15);
        List<Order> unpaidOrders = orderRepository.findByStatusAndCreateionDateBefore(OrderStatus.PENDING_PAYMENT, fifteenMinutesAgo);
        // 有数据
        if(!unpaidOrders.isEmpty()){
            for(Order order: unpaidOrders){
                order.setStatus(OrderStatus.CANCELLED);
            }
            orderRepository.saveAll(unpaidOrders);
        } else {
            log.info("[定时任务]未发现支付超时的订单。");
        }
    }

    @Transactional
    @SchedulerLock(name = "issueTicket_#{#id}", lockAtMostFor = "60s")
    public void requestTicketIssuance(Long id) {
        log.info("开始处理订单出票请求，订单ID: {}", id);
        Optional<Order> optOrder = orderRepository.findById(id);
        Order order = optOrder.orElseThrow(() -> new RuntimeException("订单不存在，订单ID: " + id));
        
        int maxRetries = 3;
        int retryCount = 0;
        boolean success = false;
        
        while (retryCount < maxRetries && !success) {
            try {
                // 更新订单状态为出票中
                order.setStatus(OrderStatus.TICKETING_IN_PROGRESS);
                orderRepository.save(order);
                // 调用航司API出票
                String ticketNumber = airlineApiClient.issueTicket(id);
                log.info("订单出票成功，订单ID: {}, 票号: {}", id, ticketNumber);
                // 更新订单状态为已出票
                order.setOrderNumber(ticketNumber);
                order.setStatus(OrderStatus.TICKETED);
                orderRepository.save(order);
                success = true;
            } catch (RuntimeException e) {
                retryCount++;
                log.error("订单出票失败，订单ID: {}, 重试次数: {}, 原因: {}", id, retryCount, e.getMessage());
                if (retryCount >= maxRetries) {
                    // 更新订单状态为出票失败
                    order.setStatus(OrderStatus.TICKETING_FAILED);
                    orderRepository.save(order);
                    throw e;
                }
                // 等待一段时间后重试
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
