package com.postion.airlineorderbackend.mapper;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-18T11:31:43+0800",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.11 (Oracle Corporation)"
)
@Component
public class OrderMapperImpl implements OrderMapper {

    @Override
    public OrderDto toDto(Order order) {
        if ( order == null ) {
            return null;
        }

        OrderDto orderDto = new OrderDto();

        orderDto.setId( order.getId() );
        orderDto.setOrderNumber( order.getOrderNumber() );
        orderDto.setStatus( order.getStatus() );
        orderDto.setAmount( order.getAmount() );
        orderDto.setCreationDate( order.getCreationDate() );
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
