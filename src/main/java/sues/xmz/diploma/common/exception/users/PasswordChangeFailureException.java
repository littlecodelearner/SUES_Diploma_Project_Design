package sues.xmz.diploma.common.exception.users;

import org.springframework.http.HttpStatus;
import sues.xmz.diploma.common.domain.ResultCode;
import sues.xmz.diploma.common.exception.BusinessException;

public class PasswordChangeFailureException extends BusinessException {
    public PasswordChangeFailureException() {
        super(ResultCode.PASSWORD_CHANGE_FAILURE, HttpStatus.BAD_REQUEST);
    }
}
