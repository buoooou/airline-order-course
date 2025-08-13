package com.postion.airlineorderbackend.service;
import com.postion.airlineorderbackend.dto.RefundDto;
import com.postion.airlineorderbackend.mapper.RefundMapper;
import com.postion.airlineorderbackend.repo.RefundRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RefundService {

    private RefundRepository refundRepository;
    private RefundMapper refundMapper;

    @Transactional(readOnly = true)
    public List<RefundDto> getAllRefunds() {
        return refundRepository.findAll().stream()
                .map(refundMapper::toDto)
                .collect(Collectors.toList());
    }
}
