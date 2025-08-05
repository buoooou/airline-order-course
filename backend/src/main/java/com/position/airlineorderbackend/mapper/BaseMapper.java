package com.position.airlineorderbackend.mapper;

import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * 基础映射器接口，提供通用的映射方法
 * @param <E> 实体类型
 * @param <D> DTO类型
 */
public interface BaseMapper<E, D> {

    /**
     * 将实体转换为DTO
     */
    D toDto(E entity);

    /**
     * 将DTO转换为实体
     */
    E toEntity(D dto);

    /**
     * 更新实体（忽略null值）
     */
    void updateEntity(@MappingTarget E entity, D dto);

    /**
     * 将实体列表转换为DTO列表
     */
    List<D> toDtoList(List<E> entities);

    /**
     * 将DTO列表转换为实体列表
     */
    List<E> toEntityList(List<D> dtos);

    /**
     * 默认的映射配置
     */
    default NullValuePropertyMappingStrategy getNullValuePropertyMappingStrategy() {
        return NullValuePropertyMappingStrategy.IGNORE;
    }
} 