package com.postion.airlineorderbackend.repo;

import com.postion.airlineorderbackend.model.Ticket;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, String> {
    Optional<Ticket> findByOrderItemId(Long orderItemId);
}