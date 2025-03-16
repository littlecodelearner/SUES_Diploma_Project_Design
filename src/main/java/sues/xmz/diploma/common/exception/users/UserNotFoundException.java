package sues.xmz.diploma.common.exception.users;

import org.springframework.http.HttpStatus;
import sues.xmz.diploma.common.domain.ResultCode;
import sues.xmz.diploma.common.exception.BusinessException;


public class UserNotFoundException extends BusinessException {
    public UserNotFoundException() {
        super(ResultCode.USER_NOT_FOUND.getCode(), ResultCode.USER_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND);
    }
}