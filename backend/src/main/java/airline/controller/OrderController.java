package airline.controller;

import com.airline.dto.OrderDetailDto;
import com.airline.dto.OrderListDto;
import com.airline.dto.PageParam;
import com.airline.entity.Order;
import com.airline.enums.OrderStatus;
import com.airline.mapper.OrderMapper;
import com.airline.service.OrderService;
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

    @PostMapping
    public Page<OrderListDto> list(@RequestBody PageParam param) {
        PageRequest pagable = PageRequest.of(param.getPageNum(), param.getPageSize());
        return orderService.list(pagable).map(mapper::toListDto);
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