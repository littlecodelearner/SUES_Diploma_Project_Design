package sues.xmz.diploma.common.exception.exercise_types;

import org.springframework.http.HttpStatus;
import sues.xmz.diploma.common.domain.ResultCode;
import sues.xmz.diploma.common.exception.BusinessException;

public class ExerciseTypesDeleteFailureException extends BusinessException {
    public ExerciseTypesDeleteFailureException() {
        super(ResultCode.EXERCISE_TYPES_DELETE_FAILURE, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
