package com.postion.airlineorderbackend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.postion.airlineorderbackend.entity.Order;
import com.postion.airlineorderbackend.entity.User;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
}