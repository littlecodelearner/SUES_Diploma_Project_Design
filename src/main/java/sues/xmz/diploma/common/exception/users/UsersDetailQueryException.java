package sues.xmz.diploma.common.exception.users;

import org.springframework.http.HttpStatus;
import sues.xmz.diploma.common.domain.ResultCode;
import sues.xmz.diploma.common.exception.BusinessException;

public class UsersDetailQueryException extends BusinessException{
    public UsersDetailQueryException() {
        super(ResultCode.USERS_DETAIL_QUERY_FAILURE, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
