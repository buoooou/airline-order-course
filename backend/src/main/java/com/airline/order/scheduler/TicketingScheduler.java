package com.airline.order.scheduler;

import com.airline.order.entity.Order;
import com.airline.order.entity.SeatDetail;
import com.airline.order.entity.SeatDetail.SeatStatus;
import com.airline.order.enums.OrderStatus;
import com.airline.order.repository.OrderRepository;
import com.airline.order.repository.SeatDetailRepository;
import com.airline.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 出票定时任务
 * 定期处理已支付订单的出票状态
 */
@Component
public class TicketingScheduler {

    private static final Logger logger = LoggerFactory.getLogger(TicketingScheduler.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private SeatDetailRepository seatDetailRepository;

    /**
     * 定时处理已支付订单的出票状态
     * 每5分钟执行一次
     */
    @Scheduled(fixedRate = 300000) // 每5分钟执行一次
    @Transactional
    public void processTicketing() {
        logger.info("开始处理已支付订单的出票状态...");
        
        // 查询所有已支付且已选择座位的订单
        List<Order> paidOrders = orderRepository.findByStatus(OrderStatus.PAID);
        
        int successCount = 0;
        int failedCount = 0;
        
        for (Order order : paidOrders) {
            try {
                // 更新订单状态为出票中
                order.setStatus(OrderStatus.TICKETING_IN_PROGRESS);
                orderRepository.save(order);
                
                // 获取订单的航班ID和座位号
                Long flightId = order.getFlightInfo().getId();
                String seatNumber = order.getSeatNumber();
                
                // 查询座位详情
                SeatDetail seatDetail = seatDetailRepository.findByFlightInfoIdAndSeatNumber(flightId, seatNumber);
                
                if (seatDetail == null) {
                    logger.error("座位不存在，航班ID: {}, 座位号: {}", flightId, seatNumber);
                    order.setStatus(OrderStatus.TICKETING_FAILED);
                    orderRepository.save(order);
                    failedCount++;
                    continue;
                }
                
                // 检查座位是否已被预定
                if (seatDetail.getSeatStatus() == SeatStatus.AVAILABLE) {
                    // 座位可用，更新座位状态为已占用
                    seatDetail.setSeatStatus(SeatStatus.OCCUPIED);
                    seatDetailRepository.save(seatDetail);
                    
                    // 更新订单状态为已出票
                    order.setStatus(OrderStatus.TICKETED);
                    orderRepository.save(order);
                    
                    logger.info("订单出票成功，订单号: {}, 航班ID: {}, 座位号: {}", 
                            order.getOrderNumber(), flightId, seatNumber);
                    successCount++;
                } else {
                    // 座位已被预定，更新订单状态为出票失败
                    order.setStatus(OrderStatus.TICKETING_FAILED);
                    orderRepository.save(order);
                    
                    logger.warn("座位已被预定，出票失败，订单号: {}, 航班ID: {}, 座位号: {}", 
                            order.getOrderNumber(), flightId, seatNumber);
                    failedCount++;
                }
            } catch (Exception e) {
                logger.error("处理订单出票时发生错误，订单ID: " + order.getId(), e);
                // 发生异常时，将订单状态设置为出票失败
                order.setStatus(OrderStatus.TICKETING_FAILED);
                orderRepository.save(order);
                failedCount++;
            }
        }
        
        logger.info("订单出票处理完成，成功: {}，失败: {}", successCount, failedCount);
    }
    
    /**
     * 定时重试出票失败的订单
     * 每30分钟执行一次
     */
    @Scheduled(fixedRate = 1800000) // 每30分钟执行一次
    @Transactional
    public void retryFailedTicketing() {
        logger.info("开始重试出票失败的订单...");
        
        // 查询所有出票失败的订单
        List<Order> failedOrders = orderRepository.findFailedTicketingOrders();
        
        int successCount = 0;
        int failedCount = 0;
        
        for (Order order : failedOrders) {
            try {
                // 更新订单状态为出票中
                order.setStatus(OrderStatus.TICKETING_IN_PROGRESS);
                orderRepository.save(order);
                
                // 获取订单的航班ID和座位号
                Long flightId = order.getFlightInfo().getId();
                String seatNumber = order.getSeatNumber();
                
                // 查询座位详情
                SeatDetail seatDetail = seatDetailRepository.findByFlightInfoIdAndSeatNumber(flightId, seatNumber);
                
                if (seatDetail == null) {
                    logger.error("座位不存在，航班ID: {}, 座位号: {}", flightId, seatNumber);
                    order.setStatus(OrderStatus.TICKETING_FAILED);
                    orderRepository.save(order);
                    failedCount++;
                    continue;
                }
                
                // 检查座位是否已被预定
                if (seatDetail.getSeatStatus() == SeatStatus.AVAILABLE) {
                    // 座位可用，更新座位状态为已占用
                    seatDetail.setSeatStatus(SeatStatus.OCCUPIED);
                    seatDetailRepository.save(seatDetail);
                    
                    // 更新订单状态为已出票
                    order.setStatus(OrderStatus.TICKETED);
                    orderRepository.save(order);
                    
                    logger.info("订单重试出票成功，订单号: {}, 航班ID: {}, 座位号: {}", 
                            order.getOrderNumber(), flightId, seatNumber);
                    successCount++;
                } else {
                    // 座位已被预定，更新订单状态为出票失败
                    order.setStatus(OrderStatus.TICKETING_FAILED);
                    orderRepository.save(order);
                    
                    logger.warn("座位已被预定，重试出票失败，订单号: {}, 航班ID: {}, 座位号: {}", 
                            order.getOrderNumber(), flightId, seatNumber);
                    failedCount++;
                }
            } catch (Exception e) {
                logger.error("重试订单出票时发生错误，订单ID: " + order.getId(), e);
                // 发生异常时，将订单状态设置为出票失败
                order.setStatus(OrderStatus.TICKETING_FAILED);
                orderRepository.save(order);
                failedCount++;
            }
        }
        
        logger.info("订单重试出票处理完成，成功: {}，失败: {}", successCount, failedCount);
    }
}