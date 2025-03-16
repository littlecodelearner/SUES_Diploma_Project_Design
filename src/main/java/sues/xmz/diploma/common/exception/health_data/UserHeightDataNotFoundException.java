package sues.xmz.diploma.common.exception.health_data;

import org.springframework.http.HttpStatus;
import sues.xmz.diploma.common.domain.ResultCode;
import sues.xmz.diploma.common.exception.BusinessException;

public class UserHeightDataNotFoundException extends BusinessException {
    public UserHeightDataNotFoundException() {
        super(ResultCode.USER_HEIGHT_DATA_NOT_FOUND_FAILURE, HttpStatus.NOT_FOUND);
    }
}

