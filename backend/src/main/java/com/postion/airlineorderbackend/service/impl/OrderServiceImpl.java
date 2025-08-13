package com.postion.airlineorderbackend.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postion.airlineorderbackend.adapter.outbound.AirlineApiClient;
import com.postion.airlineorderbackend.dto.OrderDetailDto;
import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.dto.TicketInfo;
import com.postion.airlineorderbackend.enums.TicketStatusErrorCode;
import com.postion.airlineorderbackend.exception.TicketStatusException;
import com.postion.airlineorderbackend.mapper.OrderMapper;
import com.postion.airlineorderbackend.model.Flight;
import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.OrderItem;
import com.postion.airlineorderbackend.model.OrderStatus;
import com.postion.airlineorderbackend.repo.FlightRepository;
import com.postion.airlineorderbackend.repo.OrderItemRepository;
import com.postion.airlineorderbackend.repo.OrderRepository;
import com.postion.airlineorderbackend.repo.PassengerRepository;
import com.postion.airlineorderbackend.repo.TicketRepository;
import com.postion.airlineorderbackend.service.OrderService;
import com.postion.airlineorderbackend.service.TicketService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final FlightRepository flightRepository;
    private final PassengerRepository passengerRepository;
    private final TicketRepository ticketRepository;
    private final AirlineApiClient airlineApiClient;
    private final OrderMapper OrderMapper;
    private final TicketService ticketService;

    @Override
    public OrderDto createOrder(OrderDto orderDto) {
        Order order = OrderMapper.toEntity(orderDto);
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setCreationDate(LocalDateTime.now());
        order = orderRepository.save(order);
        return OrderMapper.toDto(order);
    }

    @Override
    public OrderDto updateOrder(Long id, OrderDto orderDto) {
        Optional<Order> optOrder = orderRepository.findById(id);
        if (optOrder.isPresent()) {
            Order order = OrderMapper.toEntity(orderDto);
            order = orderRepository.save(order);
            return OrderMapper.toDto(order);
        }
        throw new RuntimeException("订单不存在，ID: " + id);
    }

    @Override
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    @Transactional
    public List<OrderDetailDto> getOrderDetails(Long id) {
        Optional<Order> optOrder = orderRepository.findById(id);
        if (!optOrder.isPresent()) {
            return null;
        }
        Order order = optOrder.get();
        Optional<List<OrderItem>> optOrderItems = orderItemRepository.findByOrderId(id);
        List<OrderDetailDto> orderDetailDtoList = new ArrayList<>();
        if(optOrderItems.isPresent()) {
            List<OrderItem> orderItems = optOrderItems.get();
            if (orderItems.isEmpty()) {
                throw new RuntimeException("订单明细为空，订单ID: " + id);
            }

            for (OrderItem item : orderItems) {
                OrderDetailDto orderDetailDto = new OrderDetailDto();
                orderDetailDto.setOrderNumber(order.getOrderNumber());
                orderDetailDto.setPaymentAmount(order.getAmount().toString());
                orderDetailDto.setPaymentMethod(order.getPaymentMethod());
                orderDetailDto.setPaymentStatus(order.getPaymentStatus());
                orderDetailDto.setPaymentTime(order.getPaymentTime());
                orderDetailDto.setStatus(order.getStatus().toString());
                orderDetailDto.setDate(order.getCreationDate());
                orderDetailDto.setUpdatedAt(order.getUpdateDate());

                flightRepository.findById(item.getFlightId()).ifPresent(flight -> {
                    orderDetailDto.setDepartureAirport(flight.getDepartureAirport());
                    orderDetailDto.setArrivalAirport(flight.getArrivalAirport());
                    orderDetailDto.setDepartureTime(flight.getDepartureTime().toString());
                    orderDetailDto.setArrivalTime(flight.getArrivalTime().toString());
                });;

                passengerRepository.findById(item.getPassengerId()).ifPresent(passenger -> {
                    orderDetailDto.setPassengerName(passenger.getName());
                    orderDetailDto.setPassengerPhone(passenger.getPhone());
                    orderDetailDto.setPassengerIdType(passenger.getIdType());
                    orderDetailDto.setPassengerId(passenger.getIdType());
                });

                ticketRepository.findByOrderItemId(item.getId()).ifPresent(ticket -> {
                    orderDetailDto.setSeatNumber(ticket.getSeatNumber());
                });

                orderDetailDtoList.add(orderDetailDto);
            }
        }
        return orderDetailDtoList;
    }

    public OrderDto getOrderById(Long id){
       Optional<Order> optOrder = orderRepository.findById(id);
        if(optOrder.isPresent()) {
            return OrderMapper.toDto(optOrder.get());
        }
        return null;
    }

    public List<OrderDto> getAllOrders(){
        return orderRepository.findAll().stream()
            .map(OrderMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public Page<OrderDto> getAllOrders(Pageable pageable) {
        log.info("查询订单列表，分页参数: {}", pageable);
        Page<Order> orders = orderRepository.findAll(pageable);
        log.info("查询结果: {} 条订单", orders.getTotalElements());
        return orders.map(OrderMapper::toDto);
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
                return OrderMapper.toDto(order);
            }
            // 状态机校验：只有 PENDING_PAYMENT 状态的订单才能支付
            if(order.getStatus() != OrderStatus.PENDING_PAYMENT){
                throw new TicketStatusException(TicketStatusErrorCode.TICKET_ISSUE_FAILED, "非待支付状态的订单，不能进行支付，当前状态：" + order.getStatus().toString());
            }

            order.setStatus(OrderStatus.PAID);
            order.setUpdateDate(LocalDateTime.now());
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

            return OrderMapper.toDto(order);
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

                // 幂等性处理：重新加载订单状态，确保获取最新数据
                order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("订单不存在，订单ID: " + id));
                if (order.getStatus() == OrderStatus.CANCELLED) {
                    log.info("订单已取消，无需重复操作，订单ID:{}", id);
                    return OrderMapper.toDto(order);
                }

                log.info("进去取消处理逻辑,状态{}", order.getStatus());
                
                order.setStatus(OrderStatus.CANCELLED);
                order.setUpdateDate(LocalDateTime.now());
                orderRepository.save(order);

                return OrderMapper.toDto(order);
            } else {
                throw new TicketStatusException(TicketStatusErrorCode.TICKET_ISSUE_FAILED, "当前状态的订单不能取消，当前状态：" + order.getStatus().toString());
            }
        } else {
           throw new RuntimeException("订单不存在，订单ID: " + id);
        }
    }


    /**
     * 定时任务：取消未支付的订单。
     * 该方法每10分钟执行一次，检查创建时间超过15分钟且状态为待支付的订单，并将其状态更新为已取消。
     * 使用分布式锁确保同一时间只有一个实例执行该任务，锁的最短持有时间为10秒，最长持有时间为55秒。
     */
    @Scheduled(fixedRate = 600000)
    @Transactional
    @SchedulerLock(name = "cancelOrderTask", lockAtLeastFor = "10s", lockAtMostFor = "55s")
    public void cancelUnpaidOrders() {
        log.info("[定时任务]检查支付超时的订单。");
        LocalDateTime fifteenMinutesAgo = LocalDateTime.now().minusMinutes(15);
        List<Order> unpaidOrders = orderRepository.findByStatusAndCreationDateBefore(OrderStatus.PENDING_PAYMENT, fifteenMinutesAgo);
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

        // 状态校验：只有 PAID 或 TICKETING_IN_PROGRESS 状态的订单才能触发机票发放
        if (order.getStatus() != OrderStatus.PAID && order.getStatus() != OrderStatus.TICKETING_IN_PROGRESS) {
            throw new IllegalStateException("Ticket issuance can only be requested for PAID or TICKETING_IN_PROGRESS orders");
        }

        try {
            // 更新订单状态为出票中
            order.setStatus(OrderStatus.TICKETING_IN_PROGRESS);
            orderRepository.save(order);

            // 获取订单项
            Optional<List<OrderItem>> optOrderItem = orderItemRepository.findByOrderId(id);
            List<OrderItem> orderItemList = optOrderItem.orElseThrow(() -> new RuntimeException("订单明细不存在，订单ID: " + id));

            // 调用航司API出票
            TicketInfo ticketInfo = airlineApiClient.issueTicket(id);
            log.info("订单出票成功，订单ID: {}, 票号: {}, 座位号: {}", id, ticketInfo.getTicketNumber(), ticketInfo.getSeatNumber());

            // 更新订单状态为已出票
            order.setStatus(OrderStatus.TICKETED);
            orderRepository.save(order);

            // 为每个订单项生成机票记录
            for (OrderItem item : orderItemList) {
                ticketService.saveTicket(
                    ticketInfo.getTicketNumber(),
                    item.getId(),
                    order.getUser().getId(),
                    item.getFlightId(),
                    ticketInfo.getSeatNumber()
                );
            }
        } catch (Exception e) {
            // 出票失败，更新订单状态为出票失败
            order.setStatus(OrderStatus.TICKETING_FAILED);
            orderRepository.save(order);
            throw new RuntimeException("订单出票失败，订单ID: " + id, e);
        }
    }
}