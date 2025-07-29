package com.postion.airlineorderbackend.model;

public enum OrderStatus {
  PENDING_PAYMENT, // pending payment
  PAID, // paid
  TICKETING_IN_PROGRESS, // ticketing in progress
  TICKETING_FAILED, // failed
  TICKETED, // ticketed(done)
  CANCELLED // cancelled
}
