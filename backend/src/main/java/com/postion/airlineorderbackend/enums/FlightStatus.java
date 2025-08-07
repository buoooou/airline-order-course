package com.postion.airlineorderbackend.enums;

/**
 * 航班状态枚举
 * 定义航班的各种运营状态
 * 
 * @author qiaozhe
 * @since 2024-01-01
 */
public enum FlightStatus {
    
    /**
     * 正常 - 航班按计划运行
     */
    ACTIVE("正常"),
    
    /**
     * 已取消 - 航班被取消
     */
    CANCELLED("已取消"),
    
    /**
     * 延误 - 航班延误
     */
    DELAYED("延误");
    
    private final String description;
    
    /**
     * 构造函数
     * @param description 状态描述
     */
    FlightStatus(String description) {
        this.description = description;
    }
    
    /**
     * 获取状态描述
     * @return 状态的中文描述
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * 检查航班是否可以预订
     * @return 是否可以预订
     */
    public boolean isBookable() {
        // 只有正常状态的航班可以预订
        return this == ACTIVE;
    }
    
    /**
     * 检查航班是否已停止服务
     * @return 是否已停止服务
     */
    public boolean isOutOfService() {
        return this == CANCELLED;
    }
    
    /**
     * 检查航班是否受到影响（延误或取消）
     * @return 是否受到影响
     */
    public boolean isAffected() {
        return this == DELAYED || this == CANCELLED;
    }
}
