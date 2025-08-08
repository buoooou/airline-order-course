package com.postion.airlineorderbackend.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import com.postion.airlineorderbackend.dto.BaseResponse;
import com.postion.airlineorderbackend.dto.ErrorResponse;

import lombok.RequiredArgsConstructor;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
	
	private final MessageSource messageSource;
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

//	请求体参数校验失败 @Valid 触发
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<BaseResponse<ErrorResponse>> handleValidationErrors(MethodArgumentNotValidException ex, Locale locale) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(error -> {
            String localizedMessage = messageSource.getMessage(error, locale);
            errors.put(error.getField(), localizedMessage);
        });

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(messageSource.getMessage("validation.failed", null, locale))
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();

        return ResponseEntity.badRequest().body(BaseResponse.error(errorResponse));
	}
	  
	@ExceptionHandler(ResponseStatusException.class) 
	public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex, Locale locale) { 
		  HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
		  String localizedMessage = messageSource.getMessage(ex.getReason(), null, ex.getReason(), locale);
		  ErrorResponse error = ErrorResponse.builder()
				  .status(status.value())
				  .error(status.getReasonPhrase())
				  .message(localizedMessage)
				  .timestamp(LocalDateTime.now().format(FORMATTER))
				  .build();
		  return ResponseEntity.status(ex.getStatusCode()).body(error); 
	 }  	  

//    通用异常处理
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleAllUncaughtException(Exception ex) {
		// 可加日志记录 ex.printStackTrace()
		ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
		return ResponseEntity.internalServerError().body(error);
	}	
	
	@ExceptionHandler(RuntimeException.class)
    public ResponseEntity<BaseResponse<String>> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(BaseResponse.failure(HttpStatus.NOT_FOUND, ex.getMessage()));
    }
	
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<BaseResponse<String>> handleBusinessException(BusinessException ex) {
	    return ResponseEntity
	            .status(HttpStatus.BAD_REQUEST)
	            .body(BaseResponse.failure(HttpStatus.BAD_REQUEST, ex.getMessage()));
	}

}
