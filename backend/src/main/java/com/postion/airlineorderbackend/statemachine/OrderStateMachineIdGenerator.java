package com.postion.airlineorderbackend.statemachine;

/**
 * 订单状态机ID生成器
 * 
 * 负责生成和管理状态机实例的唯一标识符
 * 使用订单ID作为状态机ID，格式为"order_{orderId}"
 * 
 * 设计原则：
 * 1. 唯一性：确保每个订单对应唯一的状态机实例
 * 2. 可读性：ID格式清晰，便于调试和日志追踪
 * 3. 一致性：在整个应用中使用统一的ID格式
 * 4. 兼容性：与Spring StateMachine框架兼容
 */
public class OrderStateMachineIdGenerator {
    
    /**
     * 生成状态机ID
     * 
     * 将订单ID转换为状态机实例ID
     * 格式：order_{orderId}
     * 示例：订单ID为18 -> 状态机ID为"order_18"
     * 
     * @param orderId 订单ID
     * @return 状态机实例ID
     */
    public static String generateMachineId(Long orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("订单ID不能为空");
        }
        return "order_" + orderId;
    }
    
    /**
     * 从状态机ID解析订单ID
     * 
     * 从状态机实例ID中提取原始订单ID
     * 格式：order_{orderId} -> orderId
     * 示例：状态机ID为"order_18" -> 订单ID为18
     * 
     * @param machineId 状态机实例ID
     * @return 原始订单ID
     * @throws IllegalArgumentException 如果ID格式无效
     */
    public static Long parseOrderId(String machineId) {
        if (machineId == null || !machineId.startsWith("order_")) {
            throw new IllegalArgumentException("无效的状态机ID格式: " + machineId);
        }
        try {
            return Long.parseLong(machineId.substring(6));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("无法从状态机ID解析订单ID: " + machineId, e);
        }
    }
    
    /**
     * 验证状态机ID格式
     * 
     * 检查给定的字符串是否为有效的状态机ID格式
     * 
     * @param machineId 要验证的状态机ID
     * @return true 如果格式有效，false 如果格式无效
     */
    public static boolean isValidMachineId(String machineId) {
        if (machineId == null || !machineId.startsWith("order_")) {
            return false;
        }
        try {
            Long.parseLong(machineId.substring(6));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}