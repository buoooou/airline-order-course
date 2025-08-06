package com.postion.airlineorderbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.postion.airlineorderbackend.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
