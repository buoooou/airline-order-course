package com.position.airlineorderbackend.config;

import com.position.airlineorderbackend.model.User;
import com.position.airlineorderbackend.model.Order;
import com.position.airlineorderbackend.model.FlightInfo;
import com.position.airlineorderbackend.model.OrderStatus;
import com.position.airlineorderbackend.repo.UserRepository;
import com.position.airlineorderbackend.repo.OrderRepository;
import com.position.airlineorderbackend.repo.FlightInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private FlightInfoRepository flightInfoRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        initTestUser();
        initTestFlights();
        initTestOrders();
    }
    
    private void initTestUser() {
        if (!userRepository.existsByUsername("testuser")) {
            User user = new User();
            user.setUsername("testuser");
            user.setEmail("testuser@test.com");
            user.setPassword(passwordEncoder.encode("test123"));
            user.setRole("USER");
            userRepository.save(user);
            System.out.println("testuser 创建成功");
        }
    }
    
    private void initTestFlights() {
        if (flightInfoRepository.count() == 0) {
            // 创建测试航班
            FlightInfo flight1 = new FlightInfo();
            flight1.setFlightNumber("CA1234");
            flight1.setDeparture("北京");
            flight1.setDestination("上海");
            flight1.setDepartureTime(LocalDateTime.now().plusDays(1).withHour(10).withMinute(30));
            flightInfoRepository.save(flight1);
            
            FlightInfo flight2 = new FlightInfo();
            flight2.setFlightNumber("MU5678");
            flight2.setDeparture("上海");
            flight2.setDestination("广州");
            flight2.setDepartureTime(LocalDateTime.now().plusDays(2).withHour(14).withMinute(20));
            flightInfoRepository.save(flight2);
            
            FlightInfo flight3 = new FlightInfo();
            flight3.setFlightNumber("CZ9012");
            flight3.setDeparture("广州");
            flight3.setDestination("深圳");
            flight3.setDepartureTime(LocalDateTime.now().plusDays(3).withHour(9).withMinute(15));
            flightInfoRepository.save(flight3);
            
            System.out.println("测试航班创建成功");
        }
    }
    
    private void initTestOrders() {
        User testUser = userRepository.findByUsername("testuser").orElse(null);
        if (testUser != null && orderRepository.count() == 0) {
            List<FlightInfo> flights = flightInfoRepository.findAll();
            
            // 1. 待支付订单（新创建）
            Order order1 = new Order();
            order1.setOrderNumber("TEST-PEN-001");
            order1.setStatus(OrderStatus.PENDING_PAYMENT);
            order1.setAmount(new BigDecimal("1250.75"));
            order1.setUser(testUser);
            order1.setFlightInfo(flights.get(0));
            order1.setCreatedDate(LocalDateTime.now().minusMinutes(5));
            orderRepository.save(order1);
            
            // 2. 已支付订单（等待出票）
            Order order2 = new Order();
            order2.setOrderNumber("TEST-PAI-002");
            order2.setStatus(OrderStatus.PAID);
            order2.setAmount(new BigDecimal("3400.00"));
            order2.setUser(testUser);
            order2.setFlightInfo(flights.get(1));
            order2.setCreatedDate(LocalDateTime.now().minusHours(2));
            orderRepository.save(order2);
            
            // 3. 出票中订单
            Order order3 = new Order();
            order3.setOrderNumber("TEST-TIP-003");
            order3.setStatus(OrderStatus.TICKETING_IN_PROGRESS);
            order3.setAmount(new BigDecimal("980.50"));
            order3.setUser(testUser);
            order3.setFlightInfo(flights.get(2));
            order3.setCreatedDate(LocalDateTime.now().minusHours(1));
            orderRepository.save(order3);
            
            // 4. 出票失败订单
            Order order4 = new Order();
            order4.setOrderNumber("TEST-TIF-004");
            order4.setStatus(OrderStatus.TICKETING_FAILED);
            order4.setAmount(new BigDecimal("2100.00"));
            order4.setUser(testUser);
            order4.setFlightInfo(flights.get(0));
            order4.setCreatedDate(LocalDateTime.now().minusDays(1));
            orderRepository.save(order4);
            
            // 5. 已出票订单（成功）
            Order order5 = new Order();
            order5.setOrderNumber("TEST-TIC-005");
            order5.setStatus(OrderStatus.TICKETED);
            order5.setAmount(new BigDecimal("1750.25"));
            order5.setUser(testUser);
            order5.setFlightInfo(flights.get(1));
            order5.setCreatedDate(LocalDateTime.now().minusDays(2));
            orderRepository.save(order5);
            
            // 6. 已取消订单
            Order order6 = new Order();
            order6.setOrderNumber("TEST-CAN-006");
            order6.setStatus(OrderStatus.CANCELLED);
            order6.setAmount(new BigDecimal("890.00"));
            order6.setUser(testUser);
            order6.setFlightInfo(flights.get(2));
            order6.setCreatedDate(LocalDateTime.now().minusDays(3));
            orderRepository.save(order6);
            
            System.out.println("6个测试订单创建成功，覆盖所有状态");
        }
    }
}
