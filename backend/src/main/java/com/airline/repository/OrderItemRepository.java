package com.airline.repository;

import com.airline.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId")
    List<OrderItem> findByOrderId(@Param("orderId") Long orderId);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.flight.id = :flightId")
    List<OrderItem> findByFlightId(@Param("flightId") Long flightId);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.passenger.id = :passengerId")
    List<OrderItem> findByPassengerId(@Param("passengerId") Long passengerId);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.ticketStatus = :status")
    List<OrderItem> findByTicketStatus(@Param("status") OrderItem.TicketStatus status);

    @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.flight.id = :flightId AND oi.seatClass = :seatClass")
    long countByFlightIdAndSeatClass(@Param("flightId") Long flightId, @Param("seatClass") OrderItem.SeatClass seatClass);
}