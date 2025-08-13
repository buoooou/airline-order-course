package com.position.airline_order_course.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.position.airline_order_course.dto.TicketResponse;

/*
 * 异常处理类
 */
@ControllerAdvice
public class GlobalException {

    private static final Logger logger = LoggerFactory.getLogger(GlobalException.class);

    // 处理系统异常
    @ExceptionHandler(Exception.class)
    public ResponseEntity<TicketResponse<String>> handleUnexpectedException(Exception e) {
        logger.error("系统错误", e);
        TicketResponse<String> response = TicketResponse.error(500, "系统内部错误");
        return ResponseEntity.status(500).body(response);
    }
}