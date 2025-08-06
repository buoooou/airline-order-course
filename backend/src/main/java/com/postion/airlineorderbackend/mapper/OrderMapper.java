package com.postion.airlineorderbackend.mapper;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.model.Order;

@Mapper(componentModel = "spring")
public interface OrderMapper {

  /**
   * Maps Order to OrderDto.
   * 
   * @param order Order
   * @return OrderDto
   */
  @Mappings({
      @Mapping(source = "id", target = "id"), @Mapping(source = "orderNumber", target = "orderNumber"),
      @Mapping(source = "status", target = "status"), @Mapping(source = "amount", target = "amount"),
      @Mapping(source = "creationDate", target = "creationDate"), @Mapping(source = "user", target = "user"),
      @Mapping(target = "flightInfo", expression = "java(null)")
  })
  public OrderDto order2dto(Order order);

  /**
   * Maps Order list to OrderDto list.
   * 
   * @param orders Order List
   * @return OrderDto List
   */
  default public List<OrderDto> list2dto(List<Order> orders) {
    if (orders == null) {
      return null;
    }
    List<OrderDto> dtos = new ArrayList<OrderDto>();
    for (Order order : orders) {
      dtos.add(order2dto(order));
    }
    return dtos;
  }

}
