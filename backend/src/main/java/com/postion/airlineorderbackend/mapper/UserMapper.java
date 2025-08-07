package com.postion.airlineorderbackend.mapper;

import com.postion.airlineorderbackend.dto.UserDto;
import com.postion.airlineorderbackend.model.User;
import org.mapstruct.*;

/**
 * User实体与DTO之间的映射接口
 * 使用MapStruct自动生成映射实现
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * 将User实体转换为UserDto
     * @param user 用户实体
     * @return 用户DTO
     */
    UserDto toDto(User user);

    /**
     * 将UserDto转换为User实体
     * @param userDto 用户DTO
     * @return 用户实体
     */
    User toEntity(UserDto userDto);

    /**
     * 更新User实体
     * @param userDto 包含更新数据的DTO
     * @param user 要更新的实体
     * @return 更新后的实体
     */
    @Mapping(target = "id", ignore = true) // 不更新ID
    User updateUserFromDto(UserDto userDto, @MappingTarget User user);
}