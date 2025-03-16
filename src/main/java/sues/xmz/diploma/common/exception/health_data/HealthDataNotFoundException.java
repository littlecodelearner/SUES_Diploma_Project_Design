package sues.xmz.diploma.common.exception.health_data;

import org.springframework.http.HttpStatus;
import sues.xmz.diploma.common.domain.ResultCode;
import sues.xmz.diploma.common.exception.BusinessException;

public class HealthDataNotFoundException extends BusinessException {
    public HealthDataNotFoundException() {
        super(ResultCode.HEALTH_DATA_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
}