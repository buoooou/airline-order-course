package airline.controller;

import airline.dto.OrderDetailDto;
import airline.dto.OrderListDto;
import airline.dto.PageParam;
import airline.entity.Order;
import airline.enums.OrderStatus;
import airline.mapper.OrderMapper;
import airline.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    @Autowired
    private  OrderService orderService;

    @Autowired
    private  OrderMapper mapper;

    @GetMapping
    public List<OrderListDto> list() {
        PageParam param = new PageParam();
        PageRequest pagable = PageRequest.of(param.getPageNum(), param.getPageSize());
        Page<OrderListDto> dtoPage = orderService.list(pagable).map(mapper::toListDto);
        return dtoPage.getContent();
    }

    @GetMapping("/{id}")
    public OrderDetailDto detail(@PathVariable Long id) {
        return mapper.toDetailDto(orderService.findDetail(id));
    }

    @PatchMapping("/{id}/status")
    public Order updateStatus(@PathVariable Long id,
                             @RequestParam OrderStatus status) {
      return orderService.updateStatus(id, status);
    }
}