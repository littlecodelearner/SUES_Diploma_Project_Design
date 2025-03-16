package sues.xmz.diploma.common.exception.health_goals;

import org.springframework.http.HttpStatus;
import sues.xmz.diploma.common.domain.ResultCode;
import sues.xmz.diploma.common.exception.BusinessException;

public class HealthGoalDeleteFailureException extends BusinessException {
    public HealthGoalDeleteFailureException() {
        super(ResultCode.HEALTH_GOAL_DELETE_FAILURE, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
