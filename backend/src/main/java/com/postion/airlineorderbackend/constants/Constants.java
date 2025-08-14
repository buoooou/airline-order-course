package com.postion.airlineorderbackend.constants;

public interface Constants {

    String API_RES_SUCCESS  = "Success";
    String API_RES_ERROR  = "Error";

    String GLOBAL_ERROR_MSG  = "服务器内部错误。 请联系管理员。";

    String MSG_USER_NOT_FOUND  = "该用户不存在。";
    String MSG_ORDER_NOT_FOUND  = "该订单不存在。";

    String DATE_FORMAT_YMD = "yyyyMMdd";

    String GET_ALL_ORDERS_SUCCESS = "获取订单列表成功";
    String GET_ALL_ORDERS_FAIL = "获取订单列表失败";

    String GET_ORDER_SUCCESS = "获取订单详情成功";
    String GET_ORDER_FAIL = "获取订单详情失败";

    String PAY_ORDER_SUCCESS = "订单支付成功";
    String PAY_ORDER_FAIL = "订单支付失败";

    String CANCEL_ORDER_SUCCESS = "订单取消成功";
    String CANCEL_ORDER_FAIL = "订单取消失败";

    String CREATE_ORDER_SUCCESS = "订单创建成功";
    String CREATE_ORDER_FAIL = "订单创建失败";

    String UPDATE_ORDER_STATUS_SUCCESS = "订单状态更新成功";
    String UPDATE_ORDER_STATUS_FAIL = "订单状态更新失败";

    String ORDER_STATUS_INCORRECT = "订单状态不匹配，可能已被变更。请重新确认订单状态。";
    
    String LOGIN_SUCCESS = "用户认证成功";
    String LOGIN_FAIL = "用户名或密码错误";
}
