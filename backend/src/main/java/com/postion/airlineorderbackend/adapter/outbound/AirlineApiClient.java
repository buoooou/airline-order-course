package com.postion.airlineorderbackend.adapter.outbound;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.postion.airlineorderbackend.Exception.AirlineApiTimeoutException;
import com.postion.airlineorderbackend.constants.Constants;
import com.postion.airlineorderbackend.dto.ApiResponseDTO;
import com.postion.airlineorderbackend.model.OrderStatus;

@Component
public class AirlineApiClient {

    public ApiResponseDTO<OrderStatus> communicate(String orderNumber, OrderStatus preStatus) {
        try {
            TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(1,5));
        } catch(InterruptedException e) {
            throw new RuntimeException(e);
        }
        return simulateApiResult(preStatus);
    }

    private ApiResponseDTO<OrderStatus> simulateApiResult(OrderStatus preStatus) {
        if (ThreadLocalRandom.current().nextInt(100) < 25) {
            throw new AirlineApiTimeoutException(HttpStatus.GATEWAY_TIMEOUT, "航司API响应超时");
        } else if (ThreadLocalRandom.current().nextInt(100) < 75) {
            return simulateSuccessResponse(preStatus);
        } else {
            return simulateErrorResponse(preStatus);
        }
    }

    private ApiResponseDTO<OrderStatus> simulateSuccessResponse(OrderStatus preStatus) {
        
        return ApiResponseDTO.success(HttpStatus.OK.value(), Constants.UPDATE_ORDER_STATUS_SUCCESS, updatedStatus(preStatus, true));
    }

    private ApiResponseDTO<OrderStatus> simulateErrorResponse(OrderStatus preStatus) {
        int errorType =  ThreadLocalRandom.current().nextInt(3);
        switch (errorType) {
            case 0:
                return ApiResponseDTO.error(503, "无法处理请求", updatedStatus(preStatus, false));
            case 1:
                return ApiResponseDTO.error(401, "无效API密钥", updatedStatus(preStatus, false)); 
            default:
                return ApiResponseDTO.error(500, "航司系统内部错误", updatedStatus(preStatus, false));
        }
    }

    private OrderStatus updatedStatus(OrderStatus preStatus, boolean isSuccess) {
        switch (preStatus) {
            case NONE:
                return isSuccess ? OrderStatus.PENDING_PAYMENT : preStatus; 
            case PENDING_PAYMENT:
                return isSuccess ? OrderStatus.PAID : preStatus; 
            case PAID:
                return isSuccess ? OrderStatus.TICKETING_IN_PROGRESS : preStatus; 
            case TICKETING_IN_PROGRESS:
                return isSuccess ? OrderStatus.TICKETED : OrderStatus.TICKETING_FAILED; 
            default:
                return OrderStatus.CANCELED;
        }
    }
}
