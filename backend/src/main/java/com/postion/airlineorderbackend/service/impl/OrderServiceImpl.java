package com.postion.airlineorderbackend.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

import com.postion.airlineorderbackend.model.Order;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

import com.postion.airlineorderbackend.adapter.outbound.AirlineApiClient;
import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.dto.UserDto;
import com.postion.airlineorderbackend.exception.BussinessException;
import com.postion.airlineorderbackend.mapper.OrderMapper;
import com.postion.airlineorderbackend.model.OrderStatus;
import com.postion.airlineorderbackend.repo.OrderRepository;
import com.postion.airlineorderbackend.service.OrderService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{
    
  private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

  private final OrderRepository orderRepository;

  private final AirlineApiClient airlineApiService;
  
  private final OrderMapper orderMapper;

  public List<OrderDto> getAllOrders() {
   log.info("订单列表取得");
   List<OrderDto> orderDtoList  = new ArrayList<>();
    for (int i = 0; i < orderRepository.findAll().size(); i++) {
//        orderDtoList.add(orderMapper.toOrderDto(orderRepository.findAll().get(i)));
    	orderDtoList.add(mappOrderDto(orderRepository.findAll().get(i)));
    }
    return orderDtoList;
  }
  
   public OrderDto getOrderById(Long id) {
    Order order = orderRepository.findById(id).orElse(null);
    if(order== null) {
         throw new BussinessException(HttpStatus.BAD_REQUEST,"订单不存在");
    }
//    return orderMapper.toOrderDto(order);
    return mappOrderDto(order);
   }
   
   public OrderDto payOrder(Long id) {
	   log.info("开始处理支付请求，订单ID：{}",id);
		Order order = orderRepository.findById(id).orElse(null);
	    if(order== null) {
	         throw new BussinessException(HttpStatus.BAD_REQUEST,"订单不存在");
	    }
	    //状态机校验:只有PENDING_PAYMENT状态的订单才能支付
	    if(order.getStatus()!=OrderStatus.PENDING_PAYMENT) {
	 	   log.warn("支付失败：订单{}状态不是PENDING_PAYMENT，当前状态为{}",id,order.getStatus());
	       throw new BussinessException(HttpStatus.BAD_REQUEST,"只有待支付的订单才能支付。当前状态："+order.getStatus());
	    }
	    order.setStatus(OrderStatus.PAID);
	    Order saveOrder = orderRepository.save(order);
	    log.info("订单{}状态已更新为PAID",id);
	    
	    //出票
	    requestTicketIssuance(order.getId(),saveOrder);
//	    return orderMapper.toOrderDto(saveOrder);
	    return mappOrderDto(saveOrder);
   }

  @Override
  @Async("taskExecutor")
  @Transactional
  public void requestTicketIssuance(Long id, Order saveOrder) {
    log.info("异步处理启动，订单ID：{}请求出票",id);
	Order order = orderRepository.findById(id).orElse(null);
     //状态机校验:只有PAID或TICKETING_FAILED状态的订单才能（重新）请求出票
     List<OrderStatus> vaildStatus = Arrays.asList(OrderStatus.PAID,OrderStatus.TICKETING_FAILED);
     if(!vaildStatus.contains(order.getStatus())) {
	    log.warn("出票失败，订单{}状态不是PAID或TICKETING_FAILED，当前状态为{}",id,order.getStatus());
      return;
     }
     try {
         airlineApiService.issueTicket(order.getId());
     }catch(Exception ex) {
    	 log.warn("航司出票失败，订单{}状态不是PAID或TICKETING_FAILED，当前状态为{}",id,order.getStatus());
    	 //退款，取消
     }
    order.setStatus(OrderStatus.TICKETED);
    saveOrder = orderRepository.save(order);
    log.info("订单{}状态已更新为TICKETED",id);
  }
   
  public void retryTicketingIssuance(Long id) {
    Order order = orderRepository.findById(id).orElse(null);
    if(order== null) {
        throw new IllegalArgumentException("订单不存在");
    }
//    order.setStatus(OrderStatus.TICKETING_FAILED);
//    Order saveOrder = orderRepository.save(order);
    //出票
    requestTicketIssuance(order.getId(),order);
  }
  
   public OrderDto cancelOrder(Long id) {
	   log.info("取消订单请求，订单ID：{}",id);
     Order order = orderRepository.findById(id).orElse(null);
     if(order== null) {
         throw new IllegalArgumentException("订单不存在");
     }
     List<OrderStatus> finalStatus = Arrays.asList(OrderStatus.TICKETED,OrderStatus.CANCELLED);
     if(finalStatus.contains(order.getStatus())) {
	    log.warn("取消失败，订单{}状态已处于终止（{}），无法取消",id,order.getStatus());
      throw new BussinessException(HttpStatus.BAD_REQUEST,"此状态的订单无法取消。当前状态："+order.getStatus());
     }
     order.setStatus(OrderStatus.CANCELLED);
     orderRepository.save(order);
//     return orderMapper.toOrderDto(order);
     return mappOrderDto(order);
   }
   
   public OrderDto mappOrderDto(Order order){
	   OrderDto orderDto = new OrderDto();
		  orderDto.setId(order.getId());
		  orderDto.setOrderNumber(order.getOrderNumber());
		  orderDto.setStatus(order.getStatus());
		  orderDto.setAmount(order.getAmount());
		  orderDto.setCreationDate(order.getCreationDate());
		  orderDto.setUser(new UserDto());
		  orderDto.getUser().setUsername(order.getUser().getUsername());
		  orderDto.getUser().setId(order.getUser().getId());
		  return orderDto;
   }
  
//  @Scheduled(fixedRate = 60000)
//  @Transactional
//  @SchedulerLock(
//	        name = "cancleUpaidOrdersTask",  // 锁名称（唯一标识任务）
//	        lockAtMostFor = "55s",  // 最大锁持有时间（防止死锁）
//	        lockAtLeastFor = "10s"   // 最小锁持有时间（避免高频释放/获取）
//	    )
//  public void cancleUpaidOrdersTask() {
//	  log.info("【定时任务】开始处理超时未支付订单"); 
//    LocalDateTime fifteenMinutesAgo = LocalDateTime.now().minusMinutes(15);
//    List<Order> unpaidOrders = orderRepository.findByStatusAndCreationDateBefore(OrderStatus.PENDING_PAYMENT, fifteenMinutesAgo);
//    if(unpaidOrders.isEmpty()){
//	    log.info("【定时任务】发现{}个超时订单，将它们跟更新为CANCELLED",unpaidOrders.size()); 
//      for(Order order: unpaidOrders) {
//        order.setStatus(OrderStatus.CANCELLED);
//        log.info("【定时任务】超时订单{}（创建于{}）状态已更新为CANCELLED",order.getId(),order.getCreationDate());
//      }
//      orderRepository.saveAll(unpaidOrders);
//    }else{
//	    log.info("【定时任务】未发现支付超时的订单"); 
//    }
//  }
}
