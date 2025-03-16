package sues.xmz.diploma.common.exception.health_data;

import org.springframework.http.HttpStatus;
import sues.xmz.diploma.common.domain.ResultCode;
import sues.xmz.diploma.common.exception.BusinessException;

public class HealthDataDeleteFailure extends BusinessException {
    public HealthDataDeleteFailure() {
        super(ResultCode.HEALTH_DATA_DELETE_FAILURE, HttpStatus.BAD_REQUEST);
    }
}
