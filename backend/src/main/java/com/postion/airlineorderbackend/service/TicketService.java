package com.postion.airlineorderbackend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postion.airlineorderbackend.dto.TicketDto;
import com.postion.airlineorderbackend.mapper.TicketMapper;
import com.postion.airlineorderbackend.model.Ticket;
import com.postion.airlineorderbackend.repo.TicketRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private TicketRepository ticketRepository;
    private TicketMapper ticketMapper;

    @Transactional(readOnly = true)
    public List<TicketDto> getAllTickets() {
        return ticketRepository.findAll().stream()
                .map(ticketMapper::toDto)
                .collect(Collectors.toList());
    }

    public void saveTicket(String ticketNumber, Long orderItemId, Long passengerId, Long flightId, String seatNumber) {
        Ticket ticket = new Ticket();
        ticket.setTicketNumber(ticketNumber);
        ticket.setOrderItemId(orderItemId);
        ticket.setPassengerId(passengerId);
        ticket.setFlightId(flightId);
        ticket.setSeatNumber(seatNumber);
        ticket.setStatus("ISSUED");
        ticketRepository.save(ticket);
    }

     
}