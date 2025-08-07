package com.postion.airlineorderbackend.dto;

import javax.validation.constraints.NotBlank;

/**
 * 订单状态转换请求DTO
 * 用于接收状态转换相关的请求参数
 */
public record StateTransitionRequest(
    /**
     * 状态转换事件
     * 用于指定要触发哪个状态转换操作
     * 例如：PAY（支付）、CANCEL（取消）、PROCESS_TICKETING（处理出票）等
     */
    @NotBlank(message = "状态转换事件不能为空")
    String event,

    /**
     * 操作备注
     * 可选的附加说明信息
     * 用于记录状态转换的原因或额外说明
     */
    String remark
) {
    /**
     * 紧凑构造函数，用于参数验证
     */
    public StateTransitionRequest {
        // 使用注解进行验证，构造函数中不需要额外处理
    }
}