package com.postion.airlineorderbackend.util;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import java.util.List;
import java.util.stream.Collectors;

public class ModelMapperUtil {
    private static final ModelMapper modelMapper = new ModelMapper();
    
    static {
        modelMapper.getConfiguration()
            .setMatchingStrategy(MatchingStrategies.STRICT);
    }

    public static <D> D map(Object source, Class<D> destinationType) {
        return modelMapper.map(source, destinationType);
    }

    public static <D> List<D> mapList(List<?> sources, Class<D> destinationType) {
        return sources.stream()
            .map(source -> map(source, destinationType))
            .collect(Collectors.toList());
    }
}
