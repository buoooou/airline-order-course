package com.position.airlineorderbackend.mapper;

import com.position.airlineorderbackend.model.User;
import com.position.airlineorderbackend.dto.RegisterRequest;
import com.position.airlineorderbackend.dto.AuthResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import com.position.airlineorderbackend.config.MapStructConfig;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface UserMapper {

    /**
     * 将RegisterRequest转换为User实体
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true) // 密码需要单独处理加密
    User toEntity(RegisterRequest registerRequest);

    /**
     * 将User实体转换为AuthResponse
     */
    @Mapping(target = "token", ignore = true) // token需要单独生成
    @Mapping(target = "type", constant = "Bearer")
    AuthResponse toAuthResponse(User user);

    /**
     * 更新User实体（忽略null值）
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateEntity(@MappingTarget User user, RegisterRequest registerRequest);
} 