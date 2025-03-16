package sues.xmz.diploma.common.exception.users;

import org.springframework.http.HttpStatus;
import sues.xmz.diploma.common.domain.ResultCode;
import sues.xmz.diploma.common.exception.BusinessException;

public class UserRegisterFailureException extends BusinessException {
    public UserRegisterFailureException() {
        super(ResultCode.USER_REGISTER_FAILED, HttpStatus.BAD_REQUEST);
    }
}
