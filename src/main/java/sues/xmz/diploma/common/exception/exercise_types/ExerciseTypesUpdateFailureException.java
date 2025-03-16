package sues.xmz.diploma.common.exception.exercise_types;

import org.springframework.http.HttpStatus;
import sues.xmz.diploma.common.domain.ResultCode;
import sues.xmz.diploma.common.exception.BusinessException;


public class ExerciseTypesUpdateFailureException extends BusinessException {
    public ExerciseTypesUpdateFailureException() {
        super(ResultCode.EXERCISE_TYPES_UPDATE_FAILURE, HttpStatus.BAD_REQUEST);
    }
}
