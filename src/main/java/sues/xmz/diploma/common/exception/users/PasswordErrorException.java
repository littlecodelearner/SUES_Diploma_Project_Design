package sues.xmz.diploma.common.exception.users;

import org.springframework.http.HttpStatus;
import sues.xmz.diploma.common.domain.ResultCode;
import sues.xmz.diploma.common.exception.BusinessException;

public class PasswordErrorException extends BusinessException {
    public PasswordErrorException() {
        super(ResultCode.PASSWORD_ERROR.getCode(), ResultCode.PASSWORD_ERROR.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
