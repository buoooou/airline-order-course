package com.airline.service.impl;

import com.airline.dto.OrderCreateDto;
import com.airline.dto.OrderDto;
import com.airline.entity.*;
import com.airline.exception.ResourceNotFoundException;
import com.airline.exception.ValidationException;
import com.airline.mapper.OrderMapper;
import com.airline.repository.*;
import com.airline.service.FlightService;
import com.airline.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final FlightRepository flightRepository;
    private final PassengerRepository passengerRepository;
    private final OrderMapper orderMapper;
    private final FlightService flightService;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
                           OrderItemRepository orderItemRepository,
                           UserRepository userRepository,
                           FlightRepository flightRepository,
                           PassengerRepository passengerRepository,
                           OrderMapper orderMapper,
                           FlightService flightService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
        this.flightRepository = flightRepository;
        this.passengerRepository = passengerRepository;
        this.orderMapper = orderMapper;
        this.flightService = flightService;
    }

    @Override
    public OrderDto createOrder(OrderCreateDto createDto, Long userId) {
        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
        }

        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setUser(user);
        order.setContactName(createDto.getContactName());
        order.setContactPhone(createDto.getContactPhone());
        order.setContactEmail(createDto.getContactEmail());
        order.setNotes(createDto.getNotes());
        order.setBookingDate(LocalDateTime.now());
        order.setStatus(Order.Status.PENDING);
        order.setPaymentStatus(Order.PaymentStatus.PENDING);
        order.setCurrency("CNY");

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderCreateDto.OrderItemCreateDto itemDto : createDto.getOrderItems()) {
            Flight flight = flightRepository.findById(itemDto.getFlightId())
                    .orElseThrow(() -> new ResourceNotFoundException("航班不存在"));
            
            Passenger passenger = passengerRepository.findById(itemDto.getPassengerId())
                    .orElseThrow(() -> new ResourceNotFoundException("旅客不存在"));

            if (flight.getAvailableSeats() <= 0) {
                throw new ValidationException("航班 " + flight.getFlightNumber() + " 座位已满");
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setFlight(flight);
            orderItem.setPassenger(passenger);
            orderItem.setSeatClass(itemDto.getSeatClass());
            orderItem.setTicketStatus(OrderItem.TicketStatus.BOOKED);

            BigDecimal ticketPrice = getTicketPrice(flight, itemDto.getSeatClass());
            BigDecimal taxesFees = ticketPrice.multiply(new BigDecimal("0.05")); // 5% 税费
            BigDecimal itemTotal = ticketPrice.add(taxesFees);

            orderItem.setTicketPrice(ticketPrice);
            orderItem.setTaxesFees(taxesFees);
            orderItem.setTotalPrice(itemTotal);

            orderItems.add(orderItem);
            totalAmount = totalAmount.add(itemTotal);

            // 更新航班可用座位
            flightService.updateSeatAvailability(flight.getId(), -1);
        }

        order.setOrderItems(orderItems);
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);
        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderDto> getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(orderMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderDto> getOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .map(orderMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDto> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(orderMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDto> getOrdersByUser(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable)
                .map(orderMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDto> getOrdersByStatus(Order.Status status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable)
                .map(orderMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDto> getOrdersByPaymentStatus(Order.PaymentStatus paymentStatus, Pageable pageable) {
        return orderRepository.findByPaymentStatus(paymentStatus, pageable)
                .map(orderMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDto> searchOrders(String keyword, Pageable pageable) {
        return orderRepository.findByKeyword(keyword, pageable)
                .map(orderMapper::toDto);
    }

    @Override
    public OrderDto updateOrder(Long id, OrderDto orderDto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("订单不存在"));
        
        orderMapper.updateEntityFromDto(orderDto, order);
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toDto(savedOrder);
    }

    @Override
    public OrderDto updateOrderStatus(Long id, Order.Status status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("订单不存在"));
        
        order.setStatus(status);
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toDto(savedOrder);
    }

    @Override
    public OrderDto updatePaymentStatus(Long id, Order.PaymentStatus paymentStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("订单不存在"));
        
        order.setPaymentStatus(paymentStatus);
        if (paymentStatus == Order.PaymentStatus.COMPLETED) {
            order.setPaymentTime(LocalDateTime.now());
            order.setStatus(Order.Status.PAID);
        }
        
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toDto(savedOrder);
    }

    @Override
    public void cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("订单不存在"));
        
        if (order.getStatus() == Order.Status.CANCELLED) {
            throw new ValidationException("订单已取消");
        }
        
        if (order.getPaymentStatus() == Order.PaymentStatus.COMPLETED) {
            throw new ValidationException("已支付的订单不能直接取消，请申请退款");
        }
        
        // 释放座位
        for (OrderItem item : order.getOrderItems()) {
            flightService.updateSeatAvailability(item.getFlight().getId(), 1);
            item.setTicketStatus(OrderItem.TicketStatus.CANCELLED);
        }
        
        order.setStatus(Order.Status.CANCELLED);
        orderRepository.save(order);
    }

    @Override
    public OrderDto processPayment(Long id, Order.PaymentMethod paymentMethod) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("订单不存在"));
        
        if (order.getStatus() != Order.Status.PENDING) {
            throw new ValidationException("只有待处理的订单才能支付");
        }
        
        // 模拟支付处理
        order.setPaymentMethod(paymentMethod);
        order.setPaymentStatus(Order.PaymentStatus.COMPLETED);
        order.setPaymentTime(LocalDateTime.now());
        order.setStatus(Order.Status.PAID);
        
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getOrdersBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return orderMapper.toDtoList(
                orderRepository.findByBookingDateBetween(startDate, endDate)
        );
    }

    @Override
    public void cleanupExpiredOrders() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24); // 24小时后过期
        List<Order> expiredOrders = orderRepository.findExpiredPendingOrders(cutoffTime);
        
        for (Order order : expiredOrders) {
            cancelOrder(order.getId());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long countOrdersByStatus(Order.Status status) {
        return orderRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public long countOrdersByPaymentStatus(Order.PaymentStatus paymentStatus) {
        return orderRepository.countByPaymentStatus(paymentStatus);
    }

    @Override
    public String generateOrderNumber() {
        String prefix = "ORD";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%04d", ThreadLocalRandom.current().nextInt(10000));
        return prefix + timestamp + random;
    }

    private BigDecimal getTicketPrice(Flight flight, OrderItem.SeatClass seatClass) {
        return switch (seatClass) {
            case ECONOMY -> flight.getEconomyPrice();
            case BUSINESS -> flight.getBusinessPrice();
            case FIRST -> flight.getFirstPrice();
        };
    }
}