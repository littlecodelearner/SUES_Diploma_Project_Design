package sues.xmz.diploma.common.exception.health_goals_exercise_types;

import org.springframework.http.HttpStatus;
import sues.xmz.diploma.common.domain.ResultCode;
import sues.xmz.diploma.common.exception.BusinessException;

public class HealthGoalsExerciseTypesDeleteFailureException extends BusinessException {
    public HealthGoalsExerciseTypesDeleteFailureException() {
        super(ResultCode.HEALTH_GOALS_EXERCISE_TYPES_DELETE_FAILED, HttpStatus.BAD_REQUEST);
    }
}
