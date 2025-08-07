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
import com.postion.airlineorderbackend.statemachine.OrderState;
import com.postion.airlineorderbackend.repository.FlightInfoRepository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// // 禁用嵌入式数据库替换
public class FlightInfoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FlightInfoRepository flightInfoRepository;

    private FlightInfo testFlightInfo;
    private Order testOrder;

    @BeforeEach
    public void setUp() {
        // 1. 创建并保存 FlightInfo 和关联的 Orders
        // 初始化测试数据
        testFlightInfo = new FlightInfo();
        testFlightInfo.setArrivalCity("Shanghai");
        testFlightInfo.setDepartureCity("Beijing");
        testFlightInfo.setArrivalTime(LocalDateTime.now());
        testFlightInfo.setDepartureTime(LocalDateTime.now());

        entityManager.persist(testFlightInfo);
        // entityManager.flush();

        testOrder = new Order();
        testOrder.setOrderNumber("ORD456");
        testOrder.setStatus(OrderState.PAID.name());
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
    public void whenFindById_thenReturnFlightInfo() {
        // 2. 查询 FlightInfo
        FlightInfo found = flightInfoRepository.findById(testFlightInfo.getId()).orElse(null);

        // 3. 验证结果
        assertThat(found)
                .as("查询结果不应为 null")
                .isNotNull();
        assertThat(found.getOrder())
                .as("订单不应为 null")
                .isNotNull();
        assertThat(found.getOrder().getOrderNumber())
                .as("订单号应匹配")
                .isEqualTo("ORD456");
    }
}