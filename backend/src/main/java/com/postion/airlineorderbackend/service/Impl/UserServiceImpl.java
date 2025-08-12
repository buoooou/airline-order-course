package com.postion.airlineorderbackend.service.Impl;

import com.postion.airlineorderbackend.dto.UserDTO;
import com.postion.airlineorderbackend.entity.User;
import com.postion.airlineorderbackend.repository.UserRepository;
import com.postion.airlineorderbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> new UserDTO(user.getId(), user.getUsername(), user.getRole()))
                .collect(Collectors.toList());
    }
}
