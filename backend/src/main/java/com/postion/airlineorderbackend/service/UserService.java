package com.postion.airlineorderbackend.service;

import com.postion.airlineorderbackend.dto.UserDTO;
import java.util.List;

public interface UserService {
    List<UserDTO> getAllUsers();
}
