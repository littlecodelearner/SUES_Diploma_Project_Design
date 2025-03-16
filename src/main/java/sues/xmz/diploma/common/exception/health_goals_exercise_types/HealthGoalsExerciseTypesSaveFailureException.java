package sues.xmz.diploma.common.exception.health_goals_exercise_types;

import org.springframework.http.HttpStatus;
import sues.xmz.diploma.common.domain.ResultCode;
import sues.xmz.diploma.common.exception.BusinessException;

public class HealthGoalsExerciseTypesSaveFailureException extends BusinessException {
    public HealthGoalsExerciseTypesSaveFailureException() {
        super(ResultCode.HEALTH_GOALS_EXERCISE_TYPES_SAVE_FAILED, HttpStatus.BAD_REQUEST);
    }
}
