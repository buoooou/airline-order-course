package com.postion.airlineorderbackend.component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.dto.UserDto;
import com.postion.airlineorderbackend.mapper.AandBmapper;
import com.postion.airlineorderbackend.mapper.ClassA;
import com.postion.airlineorderbackend.mapper.ClassB;
import com.postion.airlineorderbackend.mapper.OrderMapper;
import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.OrderStatus;
import com.postion.airlineorderbackend.model.User;
import com.postion.airlineorderbackend.util.JwtUtil;

import lombok.RequiredArgsConstructor;

/**
 * Only for study and test, please ignore this file.
 */
@Component
@RequiredArgsConstructor
public class DevHelper {

  @Value("${myprops.isdev}")
  private String isDev;

  private final JwtUtil jwtUtil;

  private final OrderMapper orderMapper;

  @PostConstruct
  public void init() {
    // skip if not dev
    if (!"Y".equals(isDev)) {
      return;
    }

    printJwtTokenForTest();

    printMapperTest();
  }

  /**
   * Print a JWT Token to help testing.
   */
  private void printJwtTokenForTest() {
    System.out.println("=== a jwt token for test (admin) =================");
    System.out.println(jwtUtil.genToken(
        UserDto.builder()
            .userid(1L)
            .username("admin")
            .role("ADMIN")
            .build()));

    System.out.println("=== a jwt token for test (user) =================");
    System.out.println(jwtUtil.genToken(
        UserDto.builder()
            .userid(2L)
            .username("user")
            .role("USER")
            .build()));

    System.out.println("==================================================");
  }

  private void printMapperTest() {
    System.out.println("== Mapper test ===================");
    ClassA ca = ClassA.builder().memberA("member A").memberB("member B").build();

    System.out.println("-- caToCb1 -----------");
    ClassB cb = AandBmapper.INSTANCE.caToCb1(ca);
    System.out.println(cb.getMember1());
    System.out.println(cb.getMember2());

    System.out.println("-- caToCb2 -----------");
    cb = AandBmapper.INSTANCE.caToCb2(ca);
    System.out.println(cb.getMember1());
    System.out.println(cb.getMember2());

    System.out.println("-- caListToCbList ----");
    ClassA ca2 = ClassA.builder().memberA("member A2").memberB("member B2").build();
    AandBmapper.INSTANCE.caListToCbList(Arrays.asList(ca, ca2)).forEach(cbx -> {
      System.out.println(cbx.getMember1());
      System.out.println(cbx.getMember2());
    });

    System.out.println("-- OrderMapper --------");
    Order order1 = Order.builder().id(1L).amount(BigDecimal.valueOf(1234L)).orderNumber("order1")
        .status(OrderStatus.PAID).user(User.builder().id(1L).username("test1").build()).build();
    Order order2 = Order.builder().id(2L).amount(BigDecimal.valueOf(3563L)).orderNumber("order2")
        .status(OrderStatus.PENDING_PAYMENT).user(User.builder().id(2L).username("test2").build()).build();
    List<OrderDto> dtos = orderMapper.list2dto(Arrays.asList(order1, order2));
    dtos.forEach(orderDto -> System.out.println(orderDto.getOrderNumber()));

    System.out.println("==================================");
  }

}
