package com.postion.airlineorderbackend.component;

import javax.persistence.PersistenceException;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.postion.airlineorderbackend.service.OrderService;

import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

@Component
@RequiredArgsConstructor
public class ScheduledTask {

  private final OrderService orderService;

  /**
   * Scheduled task: Payment Expiring.
   */
  @Scheduled(initialDelay = 5000, fixedDelay = 5000)
  @SchedulerLock(name = "paymentExpiring", lockAtMostFor = "60s", lockAtLeastFor = "5s")
  public void paymentExpiring() {
    try {
      orderService.cancelPaymentExpiredTickets();
      System.out.println("paymentExpiring run~");
    } catch (PersistenceException e) {
      e.printStackTrace();
      System.out.println("Payment Expiring failed.");
    }
  }

  /**
   * Scheduled task: Request ticket.
   */
  @Scheduled(initialDelay = 5000, fixedDelay = 5000)
  @SchedulerLock(name = "requestTicket", lockAtMostFor = "60s", lockAtLeastFor = "5s")
  public void requestTicket() {
    try {
      orderService.requestTicketIssuance();
      System.out.println("paymentExpiring run~");
    } catch (PersistenceException e) {
      e.printStackTrace();
      System.out.println("Payment Expiring failed.");
    }
  }

  /**
   * Scheduled task: Verify ticket, check ticketing is done or failed.
   */
  @Scheduled(initialDelay = 5000, fixedDelay = 5000)
  @SchedulerLock(name = "verifyTicket", lockAtMostFor = "60s", lockAtLeastFor = "5s")
  public void verityTicket() {
    try {
      orderService.verifyTicketIssuance();
      System.out.println("paymentExpiring run~");
    } catch (PersistenceException e) {
      e.printStackTrace();
      System.out.println("Payment Expiring failed.");
    }
  }

}
