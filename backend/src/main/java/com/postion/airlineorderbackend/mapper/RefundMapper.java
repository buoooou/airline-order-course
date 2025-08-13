package com.postion.airlineorderbackend.mapper;

import com.postion.airlineorderbackend.dto.RefundDto;
import com.postion.airlineorderbackend.model.Refund;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RefundMapper {
    RefundMapper INSTANCE = Mappers.getMapper(RefundMapper.class);

    RefundDto toDto(Refund refund);
    Refund toEntity(RefundDto refundDto);
}