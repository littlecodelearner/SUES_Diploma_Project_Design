package sues.xmz.diploma.common.exception.users;

import org.springframework.http.HttpStatus;
import sues.xmz.diploma.common.domain.ResultCode;
import sues.xmz.diploma.common.exception.BusinessException;

public class IncorrectOldPasswordException extends BusinessException {
    public IncorrectOldPasswordException() {
        super(ResultCode.INCORRECT_OLD_PASSWORD.getCode(),ResultCode.INCORRECT_OLD_PASSWORD.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
