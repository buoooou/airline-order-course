package com.postion.airlineorderbackend.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import com.postion.airlineorderbackend.entity.Order;
import com.postion.airlineorderbackend.entity.OrderStatus;
import com.postion.airlineorderbackend.entity.User;

import lombok.NonNull;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @NonNull
    Optional<Order> findById(@NonNull Long id);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    List<Order> findByStatusAndCreationDateBefore(OrderStatus status, LocalDateTime creationDate);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    List<Order> findByStatus(OrderStatus status);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    List<Order> findByCreationDateBefore(LocalDateTime creationDate);

}