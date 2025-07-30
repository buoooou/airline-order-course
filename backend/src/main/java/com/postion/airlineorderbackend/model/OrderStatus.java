package com.postion.airlineorderbackend.model;

public enum OrderStatus {
    NONE,
    PENDING_PAYMENT,
    PAID,
    TICKETING_IN_PROGRESS,
    TICKETING_FAILED,
    TICKETED,
    CANCELED;
}
