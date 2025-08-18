package com.postion.airlineorderbackend.mapper;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.User;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-18T15:37:05+0800",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.50.v20250729-0351, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class OrderMapperImpl implements OrderMapper {

    @Override
    public OrderDto toDto(Order order) {
        if ( order == null ) {
            return null;
        }

        OrderDto orderDto = new OrderDto();

        orderDto.setAmount( order.getAmount() );
        orderDto.setCreationDate( order.getCreationDate() );
        orderDto.setId( order.getId() );
        orderDto.setOrderNumber( order.getOrderNumber() );
        orderDto.setStatus( order.getStatus() );
        orderDto.setUser( userToUserDto( order.getUser() ) );

        return orderDto;
    }

    @Override
    public OrderDto.UserDto userToUserDto(User user) {
        if ( user == null ) {
            return null;
        }

        OrderDto.UserDto userDto = new OrderDto.UserDto();

        userDto.setId( user.getId() );
        userDto.setUsername( user.getUsername() );

        return userDto;
    }
}
