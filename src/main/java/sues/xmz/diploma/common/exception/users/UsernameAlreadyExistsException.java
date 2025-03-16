package sues.xmz.diploma.common.exception.users;

import org.springframework.http.HttpStatus;
import sues.xmz.diploma.common.domain.ResultCode;
import sues.xmz.diploma.common.exception.BusinessException;

public class UsernameAlreadyExistsException extends BusinessException {
    public UsernameAlreadyExistsException() {
        super(ResultCode.USERNAME_ALREADY_EXISTS.getCode(), ResultCode.USERNAME_ALREADY_EXISTS.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
