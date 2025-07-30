package com.postion.airlineorderbackend.service.impl;

import com.postion.airlineorderbackend.dto.UserDto;
import com.postion.airlineorderbackend.repo.UserRepository;
import com.postion.airlineorderbackend.service.IUserService;
import com.postion.airlineorderbackend.util.ModelMapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IUserServiceImpl implements IUserService {

    private final UserRepository userRepository;

    /**
     * @param username
     * @return
     */
    @Override
    public UserDto getUserByUsername(String username) {
        return ModelMapperUtil.map(
                userRepository.findByUsername(username).orElseThrow(() ->
                        new RuntimeException("Order not found")), UserDto.class);
    }

    /**
     * @return
     */
    @Override
    public List<UserDto> getAllUsers() {
        return ModelMapperUtil.mapList(userRepository.findAll(), UserDto.class);
    }

    /**
     * @param id
     * @return
     */
    @Override
    public UserDto getUserById(Long id) {
        return ModelMapperUtil.map(
                userRepository.findById(id).orElseThrow(() ->
                        new RuntimeException("Order not found")), UserDto.class);
    }
}