package com.postion.airlineorderbackend.model;

public enum OrderStatus {
    PENDING_PAYMENT,        //待支付
    PaiD,                   //已支付
    TICKETING_IN_PROGRESS,  //出票中
    TICKETING_FAILED,       //出票失败
    CANCELLED               //已取消
}
