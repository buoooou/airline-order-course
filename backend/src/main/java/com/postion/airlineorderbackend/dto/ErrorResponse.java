package com.postion.airlineorderbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@Schema(description = "错误信息")
public class ErrorResponse {
	@Schema(description = "HTTP 状态码", example = "400")
    private int status; 
	
	@Schema(description = "错误类型", example = "Bad Request")
    private String error; 
	
	@Schema(description = "详细错误信息", example = "Validation failed")
    private String message; 
	
	@Schema(description = "时间戳", example = "2025-06-13 15:00:00")
    private String timestamp;
}
