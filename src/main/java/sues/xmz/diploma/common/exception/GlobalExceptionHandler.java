package sues.xmz.diploma.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import sues.xmz.diploma.common.domain.Result;
import sues.xmz.diploma.common.domain.ResultCode;

import java.util.HashMap;
import java.util.Map;


/**
 * 全局异常处理器类
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<?>> handleBusinessException(BusinessException ex) {
        logger.error("业务逻辑异常: ", ex);
        Result<Object> result = Result.failure(ex.getCode(), ex.getMessage());
        return ResponseEntity.status(ex.getHttpStatus()).body(result);
    }

    //参数校验异常处理
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // 其他异常处理方法
    @ExceptionHandler(HttpMessageConversionException.class)
    public ResponseEntity<Result<?>> handleHttpMessageConversionException(HttpMessageConversionException ex) {
        logger.error("发生异常: ", ex);
        Result<Object> result = Result.failure(ResultCode.INTERNAL_SERVER_ERROR,"请求体中 【需要的参数没有输入】 或者是 【输入的参数类型或参数数据格式有问题】："+ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result<?>> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.error("发生异常: ", ex);
        Result<Object> result = Result.failure(ResultCode.BAD_REQUEST,ex.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<?>> handleException(Exception ex) {
        logger.error("发生异常: ", ex);
        Result<Object> result = Result.failure(ResultCode.INTERNAL_SERVER_ERROR,ex.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }
}