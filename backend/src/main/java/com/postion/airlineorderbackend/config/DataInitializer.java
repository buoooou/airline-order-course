package com.postion.airlineorderbackend.config;

import com.postion.airlineorderbackend.entity.User;
import com.postion.airlineorderbackend.entity.Order;
import com.postion.airlineorderbackend.entity.FlightInfo;
import com.postion.airlineorderbackend.enums.UserRole;
import com.postion.airlineorderbackend.enums.OrderStatus;
import com.postion.airlineorderbackend.enums.FlightStatus;
import com.postion.airlineorderbackend.repository.UserRepository;
import com.postion.airlineorderbackend.repository.OrderRepository;
import com.postion.airlineorderbackend.repository.FlightInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 数据初始化器
 * 在应用启动时自动创建默认用户
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private FlightInfoRepository flightInfoRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 检查是否已存在管理员用户
        if (!userRepository.findByUsername("admin").isPresent()) {
            // 创建管理员用户
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(UserRole.ADMIN);
            userRepository.save(admin);
            System.out.println("✅ 创建管理员用户: admin / admin123");
        } else {
            // 更新现有管理员用户的密码
            User admin = userRepository.findByUsername("admin").get();
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(UserRole.ADMIN);
            userRepository.save(admin);
            System.out.println("✅ 更新管理员用户密码: admin / admin123");
        }

        // 检查是否已存在普通用户
        if (!userRepository.findByUsername("user").isPresent()) {
            // 创建普通用户
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRole(UserRole.USER);
            userRepository.save(user);
            System.out.println("✅ 创建普通用户: user / user123");
        } else {
            // 更新现有普通用户的密码
            User user = userRepository.findByUsername("user").get();
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRole(UserRole.USER);
            userRepository.save(user);
            System.out.println("✅ 更新普通用户密码: user / user123");
        }

        // 创建测试航班数据
        createTestFlights();

        // 创建测试订单数据
        createTestOrders();

        System.out.println("🎉 数据初始化完成！");
        System.out.println("📝 可用账号:");
        System.out.println("   管理员: admin / admin123");
        System.out.println("   普通用户: user / user123");
    }

    /**
     * 创建测试航班数据
     */
    @Transactional
    private void createTestFlights() {
        // 使用Repository的deleteAll()方法来安全地清理数据
        try {
            // 1. 先删除订单数据（有外键约束）
            orderRepository.deleteAll();
            System.out.println("🧹 清理现有订单数据");
            
            // 2. 再删除航班数据
            flightInfoRepository.deleteAll();
            System.out.println("🧹 清理现有航班数据");
        } catch (Exception e) {
            System.out.println("⚠️ 清理数据时出现异常: " + e.getMessage());
        }

        // 创建测试航班数据
        LocalDateTime baseTime = LocalDateTime.now().plusDays(1);
        
        // 国内航班
        createFlight("CA1234", "中国国际航空", "PEK", "SHA", 
                    baseTime.withHour(8).withMinute(30), 
                    baseTime.withHour(11).withMinute(0), 
                    new BigDecimal("1299.00"), 180, 200, FlightStatus.ACTIVE);
        
        createFlight("MU5678", "中国东方航空", "SHA", "CAN", 
                    baseTime.withHour(14).withMinute(15), 
                    baseTime.withHour(17).withMinute(30), 
                    new BigDecimal("899.00"), 150, 180, FlightStatus.ACTIVE);
        
        createFlight("CZ9012", "中国南方航空", "CAN", "PEK", 
                    baseTime.withHour(19).withMinute(45), 
                    baseTime.withHour(22).withMinute(30), 
                    new BigDecimal("1199.00"), 120, 160, FlightStatus.ACTIVE);
        
        createFlight("3U8888", "四川航空", "CTU", "SHA", 
                    baseTime.plusDays(1).withHour(9).withMinute(20), 
                    baseTime.plusDays(1).withHour(12).withMinute(10), 
                    new BigDecimal("799.00"), 140, 170, FlightStatus.ACTIVE);
        
        createFlight("HU7777", "海南航空", "HAK", "PEK", 
                    baseTime.plusDays(1).withHour(16).withMinute(30), 
                    baseTime.plusDays(1).withHour(20).withMinute(15), 
                    new BigDecimal("1599.00"), 100, 150, FlightStatus.ACTIVE);
        
        // 一些不同状态的航班
        createFlight("CA5555", "中国国际航空", "PEK", "CAN", 
                    baseTime.minusHours(2), 
                    baseTime.plusHours(1), 
                    new BigDecimal("1399.00"), 0, 180, FlightStatus.ACTIVE);
        
        createFlight("MU6666", "中国东方航空", "SHA", "CTU", 
                    baseTime.minusHours(4), 
                    baseTime.minusHours(1), 
                    new BigDecimal("999.00"), 0, 160, FlightStatus.CANCELLED);
        
        createFlight("CZ7777", "中国南方航空", "CAN", "SHA", 
                    baseTime.plusDays(2).withHour(10).withMinute(0), 
                    baseTime.plusDays(2).withHour(12).withMinute(45), 
                    new BigDecimal("1099.00"), 80, 180, FlightStatus.DELAYED);

        System.out.println("✅ 创建了8个测试航班");
    }

    /**
     * 创建单个航班
     */
    private void createFlight(String flightNumber, String airline, String departureAirport, 
                             String arrivalAirport, LocalDateTime departureTime, LocalDateTime arrivalTime,
                             BigDecimal price, int availableSeats, int totalSeats, FlightStatus status) {
        FlightInfo flight = new FlightInfo();
        flight.setFlightNumber(flightNumber);
        flight.setAirline(airline);
        flight.setDepartureAirport(departureAirport);
        flight.setArrivalAirport(arrivalAirport);
        flight.setDepartureTime(departureTime);
        flight.setArrivalTime(arrivalTime);
        flight.setPrice(price);
        flight.setAvailableSeats(availableSeats);
        flight.setTotalSeats(totalSeats);
        flight.setStatus(status);
        flight.setAircraftType("A320");
        
        flightInfoRepository.save(flight);
    }

    /**
     * 创建测试订单数据
     */
    private void createTestOrders() {
        // 获取用户
        User adminUser = userRepository.findByUsername("admin").orElse(null);
        User normalUser = userRepository.findByUsername("user").orElse(null);

        if (adminUser == null || normalUser == null) {
            System.out.println("❌ 无法创建测试订单：用户不存在");
            return;
        }

        // 获取航班信息
        java.util.List<FlightInfo> flights = flightInfoRepository.findAll();
        if (flights.isEmpty()) {
            System.out.println("❌ 无法创建测试订单：航班信息不存在");
            return;
        }

        // 订单数据已在createTestFlights中清理，直接创建新数据
        System.out.println("📝 开始创建测试订单数据...");

        // 创建基础测试订单
        createOrder(adminUser, flights.get(0), "ORD001", OrderStatus.PAID, new BigDecimal("1299.00"), LocalDateTime.now().minusDays(1));
        createOrder(adminUser, flights.get(1), "ORD002", OrderStatus.TICKETED, new BigDecimal("899.00"), LocalDateTime.now().minusDays(2));
        createOrder(normalUser, flights.get(2), "ORD003", OrderStatus.CANCELLED, new BigDecimal("1199.00"), LocalDateTime.now().minusDays(3));

        // 创建超时待支付订单（用于测试定时任务）
        createTimeoutPaymentOrder(normalUser, flights.get(0), "TEST_TIMEOUT_PAY_001", new BigDecimal("850.00"), 45, "测试用户A", "超时待支付测试订单1");
        createTimeoutPaymentOrder(normalUser, flights.get(1), "TEST_TIMEOUT_PAY_002", new BigDecimal("920.00"), 50, "测试用户B", "超时待支付测试订单2");

        // 创建超时出票中订单（用于测试定时任务）
        createTimeoutTicketingOrder(normalUser, flights.get(0), "TEST_TIMEOUT_TICKET_001", new BigDecimal("1350.00"), 85, "测试用户E", "超时出票中测试订单1");
        createTimeoutTicketingOrder(normalUser, flights.get(1), "TEST_TIMEOUT_TICKET_002", new BigDecimal("1680.00"), 75, "测试用户F", "超时出票中测试订单2");

        // 创建长时间出票失败订单（用于测试定时任务）
        createLongTimeFailedOrder(normalUser, flights.get(0), "TEST_LONG_FAILED_001", new BigDecimal("980.00"), 30, "测试用户H", "长时间出票失败测试订单1");

        // 创建正常状态订单（不会被定时任务处理）
        createNormalOrder(normalUser, flights.get(3), "TEST_NORMAL_PAY_001", OrderStatus.PENDING_PAYMENT, new BigDecimal("750.00"), 10, "正常用户A", "正常待支付订单");

        System.out.println("✅ 创建了9个测试订单，包括：");
        System.out.println("   - 3个基础订单");
        System.out.println("   - 2个超时待支付订单（用于测试取消超时待支付任务）");
        System.out.println("   - 2个超时出票中订单（用于测试处理超时出票任务）");
        System.out.println("   - 1个长时间出票失败订单（用于测试取消长时间失败任务）");
        System.out.println("   - 1个正常状态订单（不会被定时任务处理）");
        System.out.println("🎯 现在可以通过定时任务管理界面测试各种定时任务功能！");
    }

    /**
     * 创建单个订单
     */
    private void createOrder(User user, FlightInfo flightInfo, String orderNumber, OrderStatus status, BigDecimal amount) {
        createOrder(user, flightInfo, orderNumber, status, amount, LocalDateTime.now().minusDays((long) (Math.random() * 30)));
    }

    /**
     * 创建单个订单（带创建时间）
     */
    private void createOrder(User user, FlightInfo flightInfo, String orderNumber, OrderStatus status, BigDecimal amount, LocalDateTime creationDate) {
        Order order = new Order();
        order.setOrderNumber(orderNumber);
        order.setStatus(status);
        order.setAmount(amount);
        order.setUser(user);
        order.setFlightInfo(flightInfo);
        order.setCreationDate(creationDate);
        order.setContactEmail(user.getUsername() + "@example.com");
        order.setContactPhone("138****" + String.format("%04d", (int) (Math.random() * 10000)));
        order.setPassengerCount(1);
        order.setPassengerNames("张三");
        
        // 根据状态设置相应的时间
        if (status == OrderStatus.PAID || status == OrderStatus.TICKETED || status == OrderStatus.CANCELLED) {
            order.setPaymentTime(order.getCreationDate().plusHours(1));
        }
        
        if (status == OrderStatus.TICKETED) {
            order.setTicketingStartTime(order.getPaymentTime().plusMinutes(30));
            order.setTicketingCompletionTime(order.getTicketingStartTime().plusMinutes(15));
        }
        
        if (status == OrderStatus.CANCELLED) {
            order.setCancellationTime(order.getCreationDate().plusHours(2));
            order.setCancellationReason("用户主动取消");
        }

        orderRepository.save(order);
    }

    /**
     * 创建超时待支付订单
     */
    private void createTimeoutPaymentOrder(User user, FlightInfo flightInfo, String orderNumber, BigDecimal amount, int minutesAgo, String passengerName, String remarks) {
        Order order = new Order();
        order.setOrderNumber(orderNumber);
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setAmount(amount);
        order.setUser(user);
        order.setFlightInfo(flightInfo);
        order.setCreationDate(LocalDateTime.now().minusMinutes(minutesAgo));
        order.setContactEmail(user.getUsername() + "@example.com");
        order.setContactPhone("139****" + String.format("%04d", (int) (Math.random() * 10000)));
        order.setPassengerCount(1);
        order.setPassengerNames(passengerName);
        order.setRemarks(remarks);

        orderRepository.save(order);
    }

    /**
     * 创建超时出票中订单
     */
    private void createTimeoutTicketingOrder(User user, FlightInfo flightInfo, String orderNumber, BigDecimal amount, int minutesAgo, String passengerName, String remarks) {
        Order order = new Order();
        order.setOrderNumber(orderNumber);
        order.setStatus(OrderStatus.TICKETING_IN_PROGRESS);
        order.setAmount(amount);
        order.setUser(user);
        order.setFlightInfo(flightInfo);
        order.setCreationDate(LocalDateTime.now().minusMinutes(minutesAgo + 10));
        order.setPaymentTime(LocalDateTime.now().minusMinutes(minutesAgo + 5));
        order.setTicketingStartTime(LocalDateTime.now().minusMinutes(minutesAgo));
        order.setContactEmail(user.getUsername() + "@example.com");
        order.setContactPhone("139****" + String.format("%04d", (int) (Math.random() * 10000)));
        order.setPassengerCount(1);
        order.setPassengerNames(passengerName);
        order.setRemarks(remarks);

        orderRepository.save(order);
    }

    /**
     * 创建长时间出票失败订单
     */
    private void createLongTimeFailedOrder(User user, FlightInfo flightInfo, String orderNumber, BigDecimal amount, int hoursAgo, String passengerName, String remarks) {
        Order order = new Order();
        order.setOrderNumber(orderNumber);
        order.setStatus(OrderStatus.TICKETING_FAILED);
        order.setAmount(amount);
        order.setUser(user);
        order.setFlightInfo(flightInfo);
        order.setCreationDate(LocalDateTime.now().minusHours(hoursAgo + 1));
        order.setPaymentTime(LocalDateTime.now().minusHours(hoursAgo).minusMinutes(30));
        order.setTicketingStartTime(LocalDateTime.now().minusHours(hoursAgo));
        order.setTicketingFailureReason("系统异常导致出票失败");
        order.setContactEmail(user.getUsername() + "@example.com");
        order.setContactPhone("139****" + String.format("%04d", (int) (Math.random() * 10000)));
        order.setPassengerCount(1);
        order.setPassengerNames(passengerName);
        order.setRemarks(remarks);

        orderRepository.save(order);
    }

    /**
     * 创建正常状态订单
     */
    private void createNormalOrder(User user, FlightInfo flightInfo, String orderNumber, OrderStatus status, BigDecimal amount, int minutesAgo, String passengerName, String remarks) {
        Order order = new Order();
        order.setOrderNumber(orderNumber);
        order.setStatus(status);
        order.setAmount(amount);
        order.setUser(user);
        order.setFlightInfo(flightInfo);
        order.setCreationDate(LocalDateTime.now().minusMinutes(minutesAgo));
        order.setContactEmail(user.getUsername() + "@example.com");
        order.setContactPhone("139****" + String.format("%04d", (int) (Math.random() * 10000)));
        order.setPassengerCount(1);
        order.setPassengerNames(passengerName);
        order.setRemarks(remarks);

        if (status == OrderStatus.TICKETING_IN_PROGRESS) {
            order.setPaymentTime(LocalDateTime.now().minusMinutes(minutesAgo - 3));
            order.setTicketingStartTime(LocalDateTime.now().minusMinutes(minutesAgo - 5));
        }

        orderRepository.save(order);
    }

    /**
     * 创建正常出票失败订单
     */
    private void createNormalFailedOrder(User user, FlightInfo flightInfo, String orderNumber, BigDecimal amount, int hoursAgo, String passengerName, String remarks) {
        Order order = new Order();
        order.setOrderNumber(orderNumber);
        order.setStatus(OrderStatus.TICKETING_FAILED);
        order.setAmount(amount);
        order.setUser(user);
        order.setFlightInfo(flightInfo);
        order.setCreationDate(LocalDateTime.now().minusHours(hoursAgo).minusMinutes(30));
        order.setPaymentTime(LocalDateTime.now().minusHours(hoursAgo).minusMinutes(20));
        order.setTicketingStartTime(LocalDateTime.now().minusHours(hoursAgo).minusMinutes(15));
        order.setTicketingFailureReason("临时系统维护");
        order.setContactEmail(user.getUsername() + "@example.com");
        order.setContactPhone("139****" + String.format("%04d", (int) (Math.random() * 10000)));
        order.setPassengerCount(1);
        order.setPassengerNames(passengerName);
        order.setRemarks(remarks);

        orderRepository.save(order);
    }
}
