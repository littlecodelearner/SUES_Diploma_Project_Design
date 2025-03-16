package sues.xmz.diploma.common.exception.health_data;

import org.springframework.http.HttpStatus;
import sues.xmz.diploma.common.domain.ResultCode;
import sues.xmz.diploma.common.exception.BusinessException;

public class HealthDataCreateFailure extends BusinessException {
    public HealthDataCreateFailure() {
        super(ResultCode.HEALTH_DATA_CREATE_FAILURE, HttpStatus.BAD_REQUEST);
    }
}
