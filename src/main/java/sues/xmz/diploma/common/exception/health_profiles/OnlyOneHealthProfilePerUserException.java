package sues.xmz.diploma.common.exception.health_profiles;

import org.springframework.http.HttpStatus;
import sues.xmz.diploma.common.domain.ResultCode;
import sues.xmz.diploma.common.exception.BusinessException;

public class OnlyOneHealthProfilePerUserException extends BusinessException {
    public OnlyOneHealthProfilePerUserException() {
        super(ResultCode.ONLY_ONE_HEALTH_PROFILE_PER_USER.getCode(), ResultCode.ONLY_ONE_HEALTH_PROFILE_PER_USER.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
