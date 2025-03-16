package sues.xmz.diploma.common.exception.users;

import org.springframework.http.HttpStatus;
import sues.xmz.diploma.common.domain.ResultCode;
import sues.xmz.diploma.common.exception.BusinessException;

public class EmailAlreadyExistsException extends BusinessException {
    public EmailAlreadyExistsException() {
        super(ResultCode.EMAIL_ALREADY_EXISTS.getCode(), ResultCode.EMAIL_ALREADY_EXISTS.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
