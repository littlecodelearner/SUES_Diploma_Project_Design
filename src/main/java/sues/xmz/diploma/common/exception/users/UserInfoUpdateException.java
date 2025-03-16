package sues.xmz.diploma.common.exception.users;

import org.springframework.http.HttpStatus;
import sues.xmz.diploma.common.domain.ResultCode;
import sues.xmz.diploma.common.exception.BusinessException;

public class UserInfoUpdateException extends BusinessException {
    public UserInfoUpdateException() {
        super(ResultCode.USER_INFO_UPDATE_FAILED, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
