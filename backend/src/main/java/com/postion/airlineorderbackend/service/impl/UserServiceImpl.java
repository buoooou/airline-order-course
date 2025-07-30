package com.postion.airlineorderbackend.service.impl;

import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.postion.airlineorderbackend.Exception.AirlineBusinessException;
import com.postion.airlineorderbackend.constants.Constants;
import com.postion.airlineorderbackend.dto.UserDTO;
import com.postion.airlineorderbackend.mapper.UserMapper;
import com.postion.airlineorderbackend.model.User;
import com.postion.airlineorderbackend.repository.UserRepository;
import com.postion.airlineorderbackend.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper = UserMapper.INSTANCE;

    @Transactional
    @Override
    public UserDTO getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(x -> userMapper.userToUserDTO(x))
                .orElseThrow(() -> new AirlineBusinessException(HttpStatus.SERVICE_UNAVAILABLE.value(), Constants.MSG_USER_NOT_FOUND));
    }

    @Deprecated
    private UserDTO mapToDTo(User entity) {
        UserDTO userDto = new UserDTO();
        userDto.setUsername(entity.getUsername());
        userDto.setPassword(entity.getPassword());
        userDto.setRole(entity.getRole());
        userDto.setCreateTime(entity.getCreateTime());
        userDto.setUpdateTime(entity.getUpdateTime());
        return userDto;
    }
}
