package com.postion.airlineorderbackend.service;


import org.springframework.stereotype.Service;

import com.postion.airlineorderbackend.dto.UserDTO;

@Service
public interface UserService {

    UserDTO getUserByUsername(String username);
}
