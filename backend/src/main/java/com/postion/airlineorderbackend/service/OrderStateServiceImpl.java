package com.postion.airlineorderbackend.service;

import org.springframework.stereotype.Service;

import com.postion.airlineorderbackend.entity.OrderStatus;

@Service
public class OrderStateServiceImpl implements IOrderStateService {

    @Override
    public boolean isValidTransition(OrderStatus current, OrderStatus next) {
        switch (current) {
            case PENDING_PAYMENT:
                return next == OrderStatus.PAID || next == OrderStatus.CANCELLED;
            case PAID:
                return next == OrderStatus.TICKETING_IN_PROGRESS || next == OrderStatus.CANCELLED;
            case TICKETING_IN_PROGRESS:
                return next == OrderStatus.TICKETED || next == OrderStatus.TICKETING_FAILED || next == OrderStatus.CANCELLED;
            case TICKETING_FAILED:
                return next == OrderStatus.TICKETING_IN_PROGRESS || next == OrderStatus.CANCELLED;
            case TICKETED:
                return next == OrderStatus.CANCELLED;
            case CANCELLED:
                return false;
            default:
                return false;
        }
    }

    @Override
    public OrderStatus updateStatus(OrderStatus current, OrderStatus next) {
        if (!isValidTransition(current, next)) {
            throw new IllegalStateException("Invalid state transition: " + current + " -> " + next);
        }
        return next;
    }
}
