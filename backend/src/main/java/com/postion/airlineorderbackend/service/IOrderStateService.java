package com.postion.airlineorderbackend.service;

import com.postion.airlineorderbackend.entity.OrderStatus;

public interface IOrderStateService {
    boolean isValidTransition(OrderStatus current, OrderStatus next);
    OrderStatus updateStatus(OrderStatus current, OrderStatus next);
}