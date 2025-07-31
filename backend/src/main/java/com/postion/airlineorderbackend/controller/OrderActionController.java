package com.postion.airlineorderbackend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("api/action")
public class OrderActionController {
    @PostMapping("/pay")
    public String pay(@RequestBody String entity) {
        //TODO: process POST request
        
        return entity;
    }

    @PostMapping("cancel")
    public String cancel(@RequestBody String entity) {
        //TODO: process POST request
        
        return entity;
    }
    
    
}
