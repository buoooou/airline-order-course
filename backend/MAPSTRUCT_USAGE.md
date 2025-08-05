# MapStruct自动化映射使用指南

## 概述

本项目已集成MapStruct自动化映射框架，用于简化实体（Entity）和DTO（Data Transfer Object）之间的转换操作。

## 功能特性

- ✅ 自动化对象映射
- ✅ 类型安全的映射
- ✅ 编译时生成映射代码
- ✅ 高性能映射
- ✅ 支持复杂映射关系
- ✅ 支持集合映射
- ✅ 支持嵌套对象映射

## 项目结构

```
mapper/
├── BaseMapper.java          # 基础映射器接口
├── OrderMapper.java         # 订单映射器
├── UserMapper.java          # 用户映射器
└── FlightInfoMapper.java    # 航班信息映射器

config/
└── MapStructConfig.java     # MapStruct全局配置
```

## 核心组件

### 1. MapStructConfig - 全局配置
```java
@MapperConfig(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface MapStructConfig {
}
```

### 2. BaseMapper - 基础映射器
```java
public interface BaseMapper<E, D> {
    D toDto(E entity);
    E toEntity(D dto);
    void updateEntity(@MappingTarget E entity, D dto);
    List<D> toDtoList(List<E> entities);
    List<E> toEntityList(List<D> dtos);
}
```

### 3. OrderMapper - 订单映射器
```java
@Mapper(config = MapStructConfig.class)
public interface OrderMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "flightInfoId", source = "flightInfo.id")
    OrderDto toDto(Order order);
    
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "flightInfo", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    Order toEntity(OrderDto orderDto);
}
```

## 映射功能

### 1. 基本映射
```java
// 实体到DTO
OrderDto orderDto = orderMapper.toDto(order);

// DTO到实体
Order order = orderMapper.toEntity(orderDto);
```

### 2. 集合映射
```java
// 实体列表到DTO列表
List<OrderDto> orderDtos = orderMapper.toDtoList(orders);

// DTO列表到实体列表
List<Order> orders = orderMapper.toEntityList(orderDtos);
```

### 3. 实体更新
```java
// 使用DTO更新实体（忽略null值）
orderMapper.updateEntity(existingOrder, updateDto);
```

### 4. 复杂映射
```java
// 嵌套对象映射
@Mapping(target = "userId", source = "user.id")
@Mapping(target = "flightInfoId", source = "flightInfo.id")
OrderDto toDto(Order order);
```

## 测试接口

### 1. 测试Order实体到DTO映射
```bash
curl -X GET http://localhost:8080/mapstruct-test/order-to-dto
```

响应示例：
```json
{
    "id": 1,
    "orderNumber": "ORD-2024-001",
    "status": "PENDING_PAYMENT",
    "amount": 1500.00,
    "createdDate": "2024-01-15T10:30:00",
    "userId": null,
    "flightInfoId": null
}
```

### 2. 测试DTO到Order实体映射
```bash
curl -X GET http://localhost:8080/mapstruct-test/dto-to-order
```

### 3. 测试Order列表映射
```bash
curl -X GET http://localhost:8080/mapstruct-test/order-list
```

### 4. 测试User映射
```bash
curl -X GET http://localhost:8080/mapstruct-test/user-mapping
```

### 5. 测试FlightInfo映射
```bash
curl -X GET http://localhost:8080/mapstruct-test/flight-info
```

### 6. 测试实体更新
```bash
curl -X GET http://localhost:8080/mapstruct-test/update-entity
```

## 在业务代码中使用

### 1. 在Service中使用
```java
@Service
public class OrderService {
    
    @Autowired
    private OrderMapper orderMapper;
    
    public OrderDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("订单", "ID", id));
        
        return orderMapper.toDto(order);
    }
    
    public List<OrderDto> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orderMapper.toDtoList(orders);
    }
    
    public OrderDto createOrder(CreateOrderRequest request) {
        Order order = orderMapper.toEntity(request);
        order = orderRepository.save(order);
        return orderMapper.toDto(order);
    }
    
    public OrderDto updateOrder(Long id, UpdateOrderRequest request) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("订单", "ID", id));
        
        orderMapper.updateEntity(order, request);
        order = orderRepository.save(order);
        return orderMapper.toDto(order);
    }
}
```

### 2. 在Controller中使用
```java
@RestController
@RequestMapping("/orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable Long id) {
        OrderDto orderDto = orderService.getOrderById(id);
        return ResponseEntity.ok(orderDto);
    }
    
    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        OrderDto orderDto = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderDto);
    }
}
```

## 映射注解说明

### 1. @Mapping
```java
@Mapping(target = "targetField", source = "sourceField")
@Mapping(target = "field", ignore = true)
@Mapping(target = "field", constant = "value")
@Mapping(target = "field", expression = "java(expression)")
```

### 2. @MappingTarget
```java
void updateEntity(@MappingTarget Order order, OrderDto orderDto);
```

### 3. 配置选项
```java
@Mapper(
    config = MapStructConfig.class,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
```

## 最佳实践

### 1. 映射器设计
- 为每个实体创建专门的映射器
- 使用全局配置保持一致性
- 明确指定需要忽略的字段

### 2. 性能优化
- MapStruct在编译时生成代码，运行时性能优异
- 避免在映射器中进行复杂业务逻辑
- 合理使用集合映射

### 3. 错误处理
- 使用@Mapping注解明确指定映射关系
- 处理null值和默认值
- 验证映射结果的正确性

### 4. 测试
- 为映射器编写单元测试
- 测试边界情况和异常情况
- 验证映射的完整性

## 配置说明

### Maven依赖
```xml
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.5.5.Final</version>
</dependency>
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct-processor</artifactId>
    <version>1.5.5.Final</version>
    <scope>provided</scope>
</dependency>
```

### 编译器配置
```xml
<annotationProcessorPaths>
    <path>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct-processor</artifactId>
        <version>1.5.5.Final</version>
    </path>
    <path>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>${lombok.version}</version>
    </path>
    <path>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok-mapstruct-binding</artifactId>
        <version>0.2.0</version>
    </path>
</annotationProcessorPaths>
```

## 注意事项

1. **编译时生成**：MapStruct在编译时生成映射代码，确保IDE正确配置注解处理器

2. **Lombok兼容性**：使用lombok-mapstruct-binding确保与Lombok的兼容性

3. **循环依赖**：避免在映射器中创建循环依赖

4. **性能考虑**：MapStruct生成的代码性能优异，适合大量对象映射

5. **调试**：生成的映射代码可以查看，便于调试和优化

## 故障排除

### 常见问题

1. **编译错误**：
   - 检查注解处理器配置
   - 验证字段名称和类型匹配

2. **映射失败**：
   - 检查@Mapping注解配置
   - 验证源对象和目标对象的字段

3. **性能问题**：
   - 避免在映射器中进行复杂操作
   - 使用批量映射而不是循环映射 