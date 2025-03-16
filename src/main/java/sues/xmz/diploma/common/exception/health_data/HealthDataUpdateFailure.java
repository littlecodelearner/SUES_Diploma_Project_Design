package sues.xmz.diploma.common.exception.health_data;

import org.springframework.http.HttpStatus;
import sues.xmz.diploma.common.domain.ResultCode;
import sues.xmz.diploma.common.exception.BusinessException;

public class HealthDataUpdateFailure extends BusinessException {
    public HealthDataUpdateFailure() {
        super(ResultCode.HEALTH_DATA_UPDATE_FAILURE, HttpStatus.BAD_REQUEST);
    }
}
