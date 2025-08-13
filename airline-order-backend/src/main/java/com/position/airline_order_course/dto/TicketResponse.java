package com.position.airline_order_course.dto;

import lombok.Data;

/*
 * 出票服务返回类
 */
@Data
public class TicketResponse<T> {
    private int code;
    private String message;
    private T data;

    public static <T> TicketResponse<T> success(T data) {
        TicketResponse<T> result = new TicketResponse<>();
        result.code = 200;
        result.message = "success";
        result.data = data;
        return result;
    }

    // 格式：{ "code": xxx, "message": "订单ORD123出票xx", "data": null }
    public static <T> TicketResponse<T> error(int code, String message) {
        TicketResponse<T> result = new TicketResponse<>();
        result.code = code;
        result.message = message;
        result.data = null;
        return result;
    }
}
