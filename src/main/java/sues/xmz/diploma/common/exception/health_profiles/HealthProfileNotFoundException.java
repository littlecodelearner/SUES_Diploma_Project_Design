package sues.xmz.diploma.common.exception.health_profiles;

import org.springframework.http.HttpStatus;
import sues.xmz.diploma.common.domain.ResultCode;
import sues.xmz.diploma.common.exception.BusinessException;

public class HealthProfileNotFoundException extends BusinessException {
    public HealthProfileNotFoundException() {
        super(ResultCode.HEALTH_PROFILE_NOT_FOUND.getCode(),ResultCode.HEALTH_PROFILE_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND);
    }
}
