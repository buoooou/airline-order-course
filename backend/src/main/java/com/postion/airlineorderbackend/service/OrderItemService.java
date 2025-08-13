package com.postion.airlineorderbackend.service;

import com.postion.airlineorderbackend.dto.OrderItemDto;
import com.postion.airlineorderbackend.mapper.OrderItemMapper;
import com.postion.airlineorderbackend.repo.OrderItemRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private OrderItemRepository orderItemRepository;
    private OrderItemMapper orderItemMapper;

    @Transactional(readOnly = true)
    public List<OrderItemDto> getAllOrderItems() {
        return orderItemRepository.findAll().stream()
                .map(orderItemMapper::toDto)
                .collect(Collectors.toList());
    }
}