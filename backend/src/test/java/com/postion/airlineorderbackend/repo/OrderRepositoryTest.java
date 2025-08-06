package com.postion.airlineorderbackend.repo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.postion.airlineorderbackend.entity.FlightInfo;
import com.postion.airlineorderbackend.entity.Order;
import com.postion.airlineorderbackend.entity.Order.OrderStatus;
import com.postion.airlineorderbackend.repository.OrderRepository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderRepository orderRepository;

    private Order testOrder;
    private FlightInfo testFlightInfo;

    @BeforeEach
    public void setUp() {
        // 初始化 FlightInfo
        testFlightInfo = new FlightInfo();
        testFlightInfo.setArrivalCity("Shanghai");
        testFlightInfo.setDepartureCity("Beijing");
        testFlightInfo.setArrivalTime(LocalDateTime.now());
        testFlightInfo.setDepartureTime(LocalDateTime.now());
        entityManager.persist(testFlightInfo);

        // 初始化 Order
        testOrder = new Order();
        testOrder.setOrderNumber("ORD456");
        testOrder.setStatus(OrderStatus.PAID);
        testOrder.setAmount(new BigDecimal("99.99"));
        testOrder.setCreationDate(LocalDateTime.now());
        testOrder.setUserId(1L);
        testOrder.setFlightId(testFlightInfo.getId());
        testOrder.setFlightInfo(testFlightInfo);
        testFlightInfo.setOrder(testOrder);

        entityManager.persist(testOrder);
        entityManager.flush();
    }

    @AfterEach
    public void tearDown() {
        // 清理测试数据
        entityManager.remove(testOrder);
        entityManager.remove(testFlightInfo);
        entityManager.flush();
    }

    @Test
    public void whenFindById_thenReturnOrder() {
        // 查询 Order
        Order found = orderRepository.findById(testOrder.getId()).orElse(null);

        // 验证结果
        assertThat(found)
                .as("查询结果不应为 null")
                .isNotNull();
        assertThat(found.getOrderNumber())
                .as("订单号应匹配")
                .isEqualTo("ORD456");
        assertThat(found.getFlightInfo())
                .as("关联的 FlightInfo 不应为 null")
                .isNotNull();
    }
}