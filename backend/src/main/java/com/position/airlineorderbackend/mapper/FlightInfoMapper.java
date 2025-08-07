package com.position.airlineorderbackend.mapper;

import com.position.airlineorderbackend.model.FlightInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import com.position.airlineorderbackend.config.MapStructConfig;
import lombok.Data;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface FlightInfoMapper {

    /**
     * 将FlightInfo实体转换为DTO（可以扩展为具体的DTO类）
     */
    FlightInfoDto toDto(FlightInfo flightInfo);

    /**
     * 将DTO转换为FlightInfo实体
     */
    @Mapping(target = "id", ignore = true)
    FlightInfo toEntity(FlightInfoDto flightInfoDto);

    /**
     * 更新FlightInfo实体（忽略null值）
     */
    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget FlightInfo flightInfo, FlightInfoDto flightInfoDto);

    /**
     * 将FlightInfo列表转换为DTO列表
     */
    List<FlightInfoDto> toDtoList(List<FlightInfo> flightInfos);

    /**
     * 将DTO列表转换为FlightInfo列表
     */
    List<FlightInfo> toEntityList(List<FlightInfoDto> flightInfoDtos);

    /**
     * FlightInfo DTO类
     */
    @Data
    class FlightInfoDto {
        private Long id;
        private String flightNumber;
        private String departure;
        private String destination;
        private String departureTime;
    }
} 