package com.postion.airlineorderbackend.mapper;

import com.postion.airlineorderbackend.dto.TicketDto;
import com.postion.airlineorderbackend.model.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TicketMapper {
    TicketMapper INSTANCE = Mappers.getMapper(TicketMapper.class);

    TicketDto toDto(Ticket ticket);
    Ticket toEntity(TicketDto ticketDto);
}