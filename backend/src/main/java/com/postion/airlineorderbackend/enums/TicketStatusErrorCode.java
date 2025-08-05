package com.postion.airlineorderbackend.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TicketStatusErrorCode {
    TICKET_ALREADY_ISSUED(4001, "机票已出票，不可重复操作"),
    TICKET_STATUS_INVALID(4002, "机票状态不合法"),
    TICKET_ISSUE_FAILED(4003, "出票失败"),
    TICKET_NOT_FOUND(4004, "机票不存在");

    private final int code;
    private final String message;
}