package com.postion.airlineorderbackend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/{id}")
    public String getAllOrders(@PathVariable String id) {
        return "order.getAllOrders()";
    }
    

}
