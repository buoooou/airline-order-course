package com.postion.airlineorderbackend.component;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.postion.airlineorderbackend.service.OrderService;

@Component
public class ScheduledTask {

  @Autowired
  private OrderService orderService;

  /**
   * Scheduled task: Payment Expiring.
   */
  @Scheduled(initialDelay = 5000, fixedDelay = 5000)
  public void paymentExpiring() {
    try {
      orderService.cancelPaymentExpiredTickets();
    } catch (PersistenceException e) {
      e.printStackTrace();
      System.out.println("Payment Expiring failed.");
    }
  }

}
