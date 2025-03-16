package sues.xmz.diploma.common.exception.users;

import org.springframework.http.HttpStatus;
import sues.xmz.diploma.common.domain.ResultCode;
import sues.xmz.diploma.common.exception.BusinessException;

public class UsersDeleteFailureException extends BusinessException {
    public UsersDeleteFailureException() {
        super(ResultCode.USERS_DELETE_FAILURE, HttpStatus.BAD_REQUEST);
    }
}
