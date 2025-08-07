package com.postion.airlineorderbackend.service.impl;

import com.postion.airlineorderbackend.converter.Converter;
import com.postion.airlineorderbackend.exception.BusinessException;
import com.postion.airlineorderbackend.model.User;
import com.postion.airlineorderbackend.repo.UserRepository;
import com.postion.airlineorderbackend.service.UserService;
import com.postion.airlineorderbackend.dto.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    /**
     * @param username
     * @return
     */
    @Override
    public UserDto getUserByUsername(String username) {
        return Converter.INSTANCE.toResUserDto(userRepository.findByUsername(username).orElseThrow(() ->
                new BusinessException(HttpStatus.BAD_REQUEST, "用户不存在")));
    }

    /**
     * @return
     */
    @Override
    public List<UserDto> getAllUsers() {
        return Converter.INSTANCE.toResUserDtoList(userRepository.findAll());
    }

    /**
     * @param id
     * @return
     */
    @Override
    public UserDto getUserById(Long id) {
        return Converter.INSTANCE.toResUserDto(
                userRepository.findById(id).orElseThrow(() ->
                        new BusinessException(HttpStatus.BAD_REQUEST, "用户不存在")));
    }

    /**
     * @param username
     * @param password
     * @return
     */
    @Override
    public UserDto validateUser(String username, String password) {
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new BusinessException(HttpStatus.BAD_REQUEST, "用户不存在"));
        boolean res = passwordEncoder.matches(password, user.getPassword());
        if(res) {
            return Converter.INSTANCE.toResUserDto(user);
        }
        return null;
    }
}