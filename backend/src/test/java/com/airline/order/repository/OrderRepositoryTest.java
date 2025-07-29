package com.airline.order.repository;

import com.airline.order.entity.Order;
import com.airline.order.entity.User;
import com.airline.order.entity.FlightInfo;
import com.airline.order.enums.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * OrderRepository 单元测试
 */
@DataJpaTest
@ActiveProfiles("test")
class OrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderRepository orderRepository;

    private User testUser1;
    private User testUser2;
    private FlightInfo testFlight1;
    private FlightInfo testFlight2;
    private Order order1;
    private Order order2;
    private Order order3;

    @BeforeEach
    void setUp() {
        // 创建测试用户
        testUser1 = new User();
        testUser1.setUsername("testuser1");
        testUser1.setPassword("password1");
        testUser1.setRole("USER");

        testUser2 = new User();
        testUser2.setUsername("testuser2");
        testUser2.setPassword("password2");
        testUser2.setRole("USER");

        entityManager.persistAndFlush(testUser1);
        entityManager.persistAndFlush(testUser2);

        // 创建测试航班信息
        testFlight1 = new FlightInfo();
        testFlight1.setFlightNumber("CA1234");
        testFlight1.setDepartureAirportCode("PEK");
        testFlight1.setDepartureAirportName("北京首都国际机场");
        testFlight1.setArrivalAirportCode("SHA");
        testFlight1.setArrivalAirportName("上海虹桥国际机场");
        testFlight1.setDepartureTime(LocalDateTime.now().plusDays(1));
        testFlight1.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(2));
        testFlight1.setFlightDuration(120);

        testFlight2 = new FlightInfo();
        testFlight2.setFlightNumber("MU5678");
        testFlight2.setDepartureAirportCode("SHA");
        testFlight2.setDepartureAirportName("上海虹桥国际机场");
        testFlight2.setArrivalAirportCode("CAN");
        testFlight2.setArrivalAirportName("广州白云国际机场");
        testFlight2.setDepartureTime(LocalDateTime.now().plusDays(2));
        testFlight2.setArrivalTime(LocalDateTime.now().plusDays(2).plusHours(2));
        testFlight2.setFlightDuration(120);

        entityManager.persistAndFlush(testFlight1);
        entityManager.persistAndFlush(testFlight2);

        // 创建测试订单
        order1 = new Order();
        order1.setOrderNumber("ORD001");
        order1.setUser(testUser1);
        order1.setFlightInfo(testFlight1);
        order1.setSeatNumber("12A");
        order1.setAmount(new BigDecimal("1500.00"));
        order1.setStatus(OrderStatus.PENDING_PAYMENT);
        order1.setCreationDate(LocalDateTime.now().minusDays(1));

        order2 = new Order();
        order2.setOrderNumber("ORD002");
        order2.setUser(testUser1);
        order2.setFlightInfo(testFlight2);
        order2.setSeatNumber("15B");
        order2.setAmount(new BigDecimal("2000.00"));
        order2.setStatus(OrderStatus.PAID);
        order2.setCreationDate(LocalDateTime.now().minusHours(12));

        order3 = new Order();
        order3.setOrderNumber("ORD003");
        order3.setUser(testUser2);
        order3.setFlightInfo(testFlight1);
        order3.setSeatNumber("12B");
        order3.setAmount(new BigDecimal("1500.00"));
        order3.setStatus(OrderStatus.TICKETED);
        order3.setCreationDate(LocalDateTime.now().minusHours(6));

        entityManager.persistAndFlush(order1);
        entityManager.persistAndFlush(order2);
        entityManager.persistAndFlush(order3);
    }

    @Test
    void testFindByOrderNumber_OrderExists() {
        // 执行测试
        Optional<Order> result = orderRepository.findByOrderNumber("ORD001");

        // 验证结果
        assertTrue(result.isPresent());
        assertEquals("ORD001", result.get().getOrderNumber());
        assertEquals(OrderStatus.PENDING_PAYMENT, result.get().getStatus());
    }

    @Test
    void testFindByOrderNumber_OrderNotExists() {
        // 执行测试
        Optional<Order> result = orderRepository.findByOrderNumber("NONEXISTENT");

        // 验证结果
        assertFalse(result.isPresent());
    }

    @Test
    void testFindByUserId() {
        // 执行测试
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> result = orderRepository.findByUserId(testUser1.getId(), pageable);

        // 验证结果
        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().stream().allMatch(order -> 
            order.getUser().getId().equals(testUser1.getId())));
    }

    @Test
    void testFindByStatus() {
        // 执行测试
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> result = orderRepository.findByStatus(OrderStatus.PENDING_PAYMENT, pageable);

        // 验证结果
        assertEquals(1, result.getContent().size());
        assertEquals(OrderStatus.PENDING_PAYMENT, result.getContent().get(0).getStatus());
    }

    @Test
    void testFindByUserIdAndStatus() {
        // 执行测试
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> result = orderRepository.findByUserIdAndStatus(
            testUser1.getId(), OrderStatus.PAID, pageable);

        // 验证结果
        assertEquals(1, result.getContent().size());
        assertEquals(OrderStatus.PAID, result.getContent().get(0).getStatus());
        assertEquals(testUser1.getId(), result.getContent().get(0).getUser().getId());
    }

    @Test
    void testFindByFlightId() {
        // 执行测试
        List<Order> result = orderRepository.findByFlightId(testFlight1.getId());

        // 验证结果
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(order -> 
            order.getFlightInfo().getId().equals(testFlight1.getId())));
    }

    @Test
    void testFindByFlightNumber() {
        // 执行测试
        List<Order> result = orderRepository.findByFlightNumber("CA1234");

        // 验证结果
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(order -> 
            "CA1234".equals(order.getFlightInfo().getFlightNumber())));
    }

    @Test
    void testFindBySeatNumber() {
        // 执行测试
        List<Order> result = orderRepository.findBySeatNumber("12A");

        // 验证结果
        assertEquals(1, result.size());
        assertEquals("12A", result.get(0).getSeatNumber());
    }

    @Test
    void testFindByFlightIdAndSeatNumber() {
        // 执行测试
        Optional<Order> result = orderRepository.findByFlightIdAndSeatNumber(
            testFlight1.getId(), "12A");

        // 验证结果
        assertTrue(result.isPresent());
        assertEquals("12A", result.get().getSeatNumber());
        assertEquals(testFlight1.getId(), result.get().getFlightInfo().getId());
    }

    @Test
    void testFindByCreationDateBetween() {
        // 执行测试
        LocalDateTime startTime = LocalDateTime.now().minusDays(2);
        LocalDateTime endTime = LocalDateTime.now();
        List<Order> result = orderRepository.findByCreationDateBetween(startTime, endTime);

        // 验证结果
        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(order -> 
            order.getCreationDate().isAfter(startTime) && 
            order.getCreationDate().isBefore(endTime)));
    }

    @Test
    void testFindByAmountBetween() {
        // 执行测试
        BigDecimal minAmount = new BigDecimal("1000.00");
        BigDecimal maxAmount = new BigDecimal("1800.00");
        List<Order> result = orderRepository.findByAmountBetween(minAmount, maxAmount);

        // 验证结果
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(order -> 
            order.getAmount().compareTo(minAmount) >= 0 && 
            order.getAmount().compareTo(maxAmount) <= 0));
    }

    @Test
    void testCountByStatus() {
        // 执行测试
        long count = orderRepository.countByStatus(OrderStatus.PENDING_PAYMENT);

        // 验证结果
        assertEquals(1, count);
    }

    @Test
    void testCountByUserId() {
        // 执行测试
        long count = orderRepository.countByUserId(testUser1.getId());

        // 验证结果
        assertEquals(2, count);
    }

    @Test
    void testFindExpiredPendingPaymentOrders() {
        // 执行测试
        LocalDateTime expiredTime = LocalDateTime.now().minusHours(1);
        List<Order> result = orderRepository.findExpiredPendingPaymentOrders(expiredTime);

        // 验证结果
        assertEquals(1, result.size());
        assertEquals(OrderStatus.PENDING_PAYMENT, result.get(0).getStatus());
        assertTrue(result.get(0).getCreationDate().isBefore(expiredTime));
    }

    @Test
    void testFindFailedTicketingOrders() {
        // 创建出票失败的订单
        Order failedOrder = new Order();
        failedOrder.setOrderNumber("FAILED001");
        failedOrder.setUser(testUser1);
        failedOrder.setFlightInfo(testFlight1);
        failedOrder.setSeatNumber("20C");
        failedOrder.setAmount(new BigDecimal("1500.00"));
        failedOrder.setStatus(OrderStatus.TICKETING_FAILED);
        failedOrder.setCreationDate(LocalDateTime.now());
        entityManager.persistAndFlush(failedOrder);

        // 执行测试
        List<Order> result = orderRepository.findFailedTicketingOrders();

        // 验证结果
        assertEquals(1, result.size());
        assertEquals(OrderStatus.TICKETING_FAILED, result.get(0).getStatus());
    }

    @Test
    void testCalculateRevenueByDateRange() {
        // 执行测试
        LocalDateTime startTime = LocalDateTime.now().minusDays(2);
        LocalDateTime endTime = LocalDateTime.now();
        BigDecimal totalRevenue = orderRepository.calculateRevenueByDateRange(startTime, endTime);

        // 验证结果 (只有PAID和TICKETED状态的订单计入收入)
        BigDecimal expectedRevenue = new BigDecimal("3500.00"); // order2 (2000) + order3 (1500)
        assertEquals(0, expectedRevenue.compareTo(totalRevenue));
    }

    @Test
    void testFindRecentOrders() {
        // 执行测试
        Pageable pageable = PageRequest.of(0, 2);
        Page<Order> result = orderRepository.findRecentOrders(pageable);

        // 验证结果
        assertEquals(2, result.getContent().size());
        // 验证按创建时间降序排列
        assertTrue(result.getContent().get(0).getCreationDate()
            .isAfter(result.getContent().get(1).getCreationDate()) ||
            result.getContent().get(0).getCreationDate()
            .equals(result.getContent().get(1).getCreationDate()));
    }

    @Test
    void testFindByUsername() {
        // 执行测试
        List<Order> result = orderRepository.findByUsername("testuser1");

        // 验证结果
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(order -> 
            "testuser1".equals(order.getUser().getUsername())));
    }

    @Test
    void testSaveOrder() {
        // 创建新订单
        Order newOrder = new Order();
        newOrder.setOrderNumber("NEW001");
        newOrder.setUser(testUser2);
        newOrder.setFlightInfo(testFlight2);
        newOrder.setSeatNumber("20A");
        newOrder.setAmount(new BigDecimal("2500.00"));
        newOrder.setStatus(OrderStatus.PENDING_PAYMENT);
        newOrder.setCreationDate(LocalDateTime.now());

        // 执行测试
        Order savedOrder = orderRepository.save(newOrder);

        // 验证结果
        assertNotNull(savedOrder.getId());
        assertEquals("NEW001", savedOrder.getOrderNumber());
        assertEquals(OrderStatus.PENDING_PAYMENT, savedOrder.getStatus());
    }

    @Test
    void testDeleteOrder() {
        // 获取订单ID
        Long orderId = order1.getId();

        // 执行删除
        orderRepository.deleteById(orderId);

        // 验证删除结果
        Optional<Order> deletedOrder = orderRepository.findById(orderId);
        assertFalse(deletedOrder.isPresent());
    }

    @Test
    void testUpdateOrder() {
        // 更新订单状态
        order1.setStatus(OrderStatus.PAID);
        Order updatedOrder = orderRepository.save(order1);

        // 验证更新结果
        assertEquals(OrderStatus.PAID, updatedOrder.getStatus());
        
        // 从数据库重新查询验证
        Optional<Order> reloadedOrder = orderRepository.findById(order1.getId());
        assertTrue(reloadedOrder.isPresent());
        assertEquals(OrderStatus.PAID, reloadedOrder.get().getStatus());
    }

    @Test
    void testOrderExists() {
        // 测试订单是否存在
        boolean exists = orderRepository.existsById(order1.getId());
        assertTrue(exists);

        // 测试不存在的订单
        boolean notExists = orderRepository.existsById(999L);
        assertFalse(notExists);
    }
}