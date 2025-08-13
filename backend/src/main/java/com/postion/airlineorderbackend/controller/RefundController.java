package com.postion.airlineorderbackend.controller;

import com.postion.airlineorderbackend.dto.RefundDto;
import com.postion.airlineorderbackend.service.RefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/refunds")
public class RefundController {

    @Autowired
    private RefundService refundService;

    @GetMapping
    public List<RefundDto> getAllRefunds() {
        return refundService.getAllRefunds();
    }
}