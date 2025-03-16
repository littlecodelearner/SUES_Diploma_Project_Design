package sues.xmz.diploma.common.exception.exercise_types;

import org.springframework.http.HttpStatus;
import sues.xmz.diploma.common.domain.ResultCode;
import sues.xmz.diploma.common.exception.BusinessException;


public class ExerciseTypesCreateFailureException extends BusinessException {
    public ExerciseTypesCreateFailureException() {
        super(ResultCode.EXERCISE_TYPES_CREATE_FAILURE, HttpStatus.BAD_REQUEST);
    }
}
