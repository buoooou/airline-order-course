package com.postion.airlineorderbackend.service;

import com.postion.airlineorderbackend.dto.RegisterRequest;

public interface UserService {

    /**
     * 用户注册
     * @param registerRequest 注册请求参数
     */
    void register(RegisterRequest registerRequest);
}
    