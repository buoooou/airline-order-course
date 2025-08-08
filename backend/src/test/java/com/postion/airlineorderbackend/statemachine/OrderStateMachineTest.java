package com.postion.airlineorderbackend.statemachine;

import com.postion.airlineorderbackend.entity.AppUser;
import com.postion.airlineorderbackend.entity.FlightInfo;
import com.postion.airlineorderbackend.entity.Order;
import com.postion.airlineorderbackend.repository.AppUserRepository;
import com.postion.airlineorderbackend.repository.FlightInfoRepository;
import com.postion.airlineorderbackend.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 订单状态机测试类
 */
@SpringBootTest
@Transactional
@Import({ TestConfig.class, OrderStateMachineConfig.class })
@ActiveProfiles("test")
class OrderStateMachineTest {

        @Autowired
        private OrderStateMachineService stateMachineService;

        @Autowired
        private OrderStateMachineUtil stateMachineUtil;

        @Autowired
        private OrderRepository orderRepository;

        @Autowired
        private AppUserRepository appUserRepository;

        @Autowired
        private FlightInfoRepository flightInfoRepository;

        private Order testOrder;
        private AppUser testUser;
        private FlightInfo testFlight;

        @BeforeEach
        void setUp() {
                // 创建测试用户
                testUser = new AppUser();
                testUser.setUsername("test-user");
                testUser.setPassword("password");
                testUser.setRole("USER");
                testUser = appUserRepository.save(testUser);

                // 创建测试航班
                testFlight = new FlightInfo();
                testFlight.setDepartureCity("北京");
                testFlight.setArrivalCity("上海");
                testFlight.setDepartureTime(LocalDateTime.now().minusHours(2));
                testFlight.setArrivalTime(LocalDateTime.now().minusMinutes(30));
                testFlight = flightInfoRepository.save(testFlight);

                // 创建测试订单
                testOrder = new Order();
                testOrder.setOrderNumber("TEST-ORDER-001");
                testOrder.setStatus(OrderState.PENDING_PAYMENT.name());
                testOrder.setAmount(new BigDecimal("1000.00"));
                testOrder.setUserId(testUser.getId());
                testOrder.setFlightId(testFlight.getId());
                testOrder.setCreationDate(LocalDateTime.now());

                testOrder = orderRepository.save(testOrder);
        }

        @Test
        void testInitialState() {
                OrderState currentState = stateMachineService.getCurrentState(testOrder.getId());
                assertEquals(OrderState.PENDING_PAYMENT, currentState);
        }

        @Test
        void testPayOrderSuccess() {
                // 打印初始状态
                System.out.println("初始订单状态: " + testOrder.getStatus());
                System.out.println("订单用户ID: " + testOrder.getUserId());
                System.out.println("测试用户ID: " + testUser.getId());

                // 创建上下文
                OrderStateContext context = new OrderStateContext(testOrder);
                context.setOperator(testUser.getUsername());

                // 执行状态转换
                boolean result = stateMachineService.triggerStateTransition(
                                testOrder.getId(),
                                OrderEvent.PAY,
                                context,
                                testUser.getId(),
                                "user");

                System.out.println("状态转换结果: " + result);

                // 重新获取订单查看最新状态
                Order updatedOrder = orderRepository.findById(testOrder.getId()).orElse(null);
                if (updatedOrder != null) {
                        System.out.println("转换后订单状态: " + updatedOrder.getStatus());
                }

                assertTrue(result);

                Order order = orderRepository.findById(testOrder.getId()).orElse(null);
                assertNotNull(order);
                assertEquals(OrderState.PAID.name(), order.getStatus());
        }

        @Test
    void testPayOrderFailure_WrongState() {
        // 先通过状态机将状态改为已支付
        OrderStateContext payContext = new OrderStateContext(testOrder);
        payContext.setOperator(testUser.getUsername());
        
        boolean paySuccess = stateMachineService.triggerStateTransition(
                testOrder.getId(), OrderEvent.PAY, payContext, testUser.getId(), "user");
        assertTrue(paySuccess);
        
        // 验证状态确实变为已支付
        assertEquals(OrderState.PAID, stateMachineService.getCurrentState(testOrder.getId()));

        // 尝试再次支付，应该失败
        OrderStateContext context = new OrderStateContext(testOrder);
        boolean success = stateMachineService.triggerStateTransition(
                testOrder.getId(), OrderEvent.PAY, context, testUser.getId(), "user");

        assertFalse(success);

        // 状态应该保持为已支付
        OrderState currentState = stateMachineService.getCurrentState(testOrder.getId());
        assertEquals(OrderState.PAID, currentState);
    }

        @Test
        void testCancelOrderSuccess() {
                OrderStateContext context = new OrderStateContext(testOrder);
                context.setOperator("test-user");

                boolean success = stateMachineService.triggerStateTransition(
                                testOrder.getId(), OrderEvent.CANCEL, context, testUser.getId(), "user");

                assertTrue(success);

                OrderState currentState = stateMachineService.getCurrentState(testOrder.getId());
                assertEquals(OrderState.CANCELLED, currentState);
        }

        @Test
        void testFullOrderLifecycle() {
                // 1. 待支付 -> 已支付
                OrderStateContext payContext = new OrderStateContext(testOrder);
                payContext.setOperator("test-user");

                boolean paySuccess = stateMachineService.triggerStateTransition(
                                testOrder.getId(), OrderEvent.PAY, payContext, testUser.getId(), "user");
                assertTrue(paySuccess);
                assertEquals(OrderState.PAID, stateMachineService.getCurrentState(testOrder.getId()));

                // 2. 已支付 -> 出票中
                OrderStateContext ticketingContext = new OrderStateContext(testOrder);
                ticketingContext.setOperator("system");

                boolean processSuccess = stateMachineService.triggerStateTransition(
                                testOrder.getId(), OrderEvent.PROCESS_TICKETING, ticketingContext, testUser.getId(),
                                "system");
                assertTrue(processSuccess);
                assertEquals(OrderState.TICKETING_IN_PROGRESS,
                                stateMachineService.getCurrentState(testOrder.getId()));

                // 3. 出票中 -> 已出票
                OrderStateContext successContext = new OrderStateContext(testOrder);
                successContext.setOperator("system");

                boolean ticketSuccess = stateMachineService.triggerStateTransition(
                                testOrder.getId(), OrderEvent.TICKETING_SUCCESS, successContext, testUser.getId(),
                                "system");
                assertTrue(ticketSuccess);
                assertEquals(OrderState.TICKETED, stateMachineService.getCurrentState(testOrder.getId()));
        }

        @Test
        void testTicketingFailedFlow() {
                // 1. 待支付 -> 已支付
                OrderStateContext payContext = new OrderStateContext(testOrder);
                stateMachineService.triggerStateTransition(testOrder.getId(), OrderEvent.PAY, payContext,
                                testUser.getId(), "user");

                // 2. 已支付 -> 出票中
                OrderStateContext processContext = new OrderStateContext(testOrder);
                stateMachineService.triggerStateTransition(testOrder.getId(),
                                OrderEvent.PROCESS_TICKETING, processContext, testUser.getId(), "system");

                // 3. 出票中 -> 出票失败
                OrderStateContext failContext = new OrderStateContext(testOrder);
                failContext.setRemark("出票系统异常");

                boolean failSuccess = stateMachineService.triggerStateTransition(
                                testOrder.getId(), OrderEvent.TICKETING_FAILURE, failContext, testUser.getId(),
                                "system");
                assertTrue(failSuccess);
                assertEquals(OrderState.TICKETING_FAILED,
                                stateMachineService.getCurrentState(testOrder.getId()));

                // 4. 出票失败 -> 出票中（重试）
                OrderStateContext retryContext = new OrderStateContext(testOrder);
                retryContext.incrementRetryCount();

                boolean retrySuccess = stateMachineService.triggerStateTransition(
                                testOrder.getId(), OrderEvent.RETRY_TICKETING, retryContext, testUser.getId(),
                                "system");
                assertTrue(retrySuccess);
                assertEquals(OrderState.TICKETING_IN_PROGRESS,
                                stateMachineService.getCurrentState(testOrder.getId()));
        }

        @Test
        void testCanExecuteEvent() {
                assertTrue(stateMachineService.canTriggerEvent(testOrder.getId(), OrderEvent.PAY));
                assertTrue(stateMachineService.canTriggerEvent(testOrder.getId(), OrderEvent.CANCEL));
                assertFalse(stateMachineService.canTriggerEvent(testOrder.getId(), OrderEvent.TICKETING_SUCCESS));
        }

        @Test
        void testStateMachineUtil() {
                var states = stateMachineUtil.getAllStates();
                assertFalse(states.isEmpty());

                var events = stateMachineUtil.getAllEvents();
                assertFalse(events.isEmpty());

                var orderDetail = stateMachineUtil.getOrderStateDetail(testOrder.getId());
                assertNotNull(orderDetail);
                assertEquals("PENDING_PAYMENT", orderDetail.get("currentState"));

                var allowedEvents = stateMachineUtil.getAllowedEvents(testOrder.getId());
                assertFalse(allowedEvents.isEmpty());
        }
}