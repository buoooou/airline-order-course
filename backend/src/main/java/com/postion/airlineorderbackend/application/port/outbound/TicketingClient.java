package com.postion.airlineorderbackend.application.port.outbound;

public interface TicketingClient {
    void issueTicket(String orderNumber) throws InterruptedException;
}
