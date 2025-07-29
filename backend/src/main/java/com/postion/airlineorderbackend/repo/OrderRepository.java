package com.postion.airlineorderbackend.repo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

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
