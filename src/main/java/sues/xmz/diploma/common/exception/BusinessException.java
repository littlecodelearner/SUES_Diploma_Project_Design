package sues.xmz.diploma.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
import sues.xmz.diploma.common.domain.ResultCode;

/**
 * 自定义业务异常基类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class BusinessException extends RuntimeException {
    private final int code;
    private final HttpStatus httpStatus;

    public BusinessException(int code, String message, HttpStatus httpStatus) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public BusinessException(ResultCode resultCode, HttpStatus httpStatus) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.httpStatus = httpStatus;
    }

}