package sues.xmz.diploma.common.exception.health_goals;

import org.springframework.http.HttpStatus;
import sues.xmz.diploma.common.domain.ResultCode;
import sues.xmz.diploma.common.exception.BusinessException;

public class HealthGoalUpdateFailureException extends BusinessException {
    public HealthGoalUpdateFailureException() {
        super(ResultCode.HEALTH_GOAL_UPDATE_FAILURE.getCode(), ResultCode.HEALTH_GOAL_UPDATE_FAILURE.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
