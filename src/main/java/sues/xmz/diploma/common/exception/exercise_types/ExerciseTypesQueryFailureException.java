package sues.xmz.diploma.common.exception.exercise_types;

import org.springframework.http.HttpStatus;
import sues.xmz.diploma.common.domain.ResultCode;
import sues.xmz.diploma.common.exception.BusinessException;

public class ExerciseTypesQueryFailureException extends BusinessException {
    public ExerciseTypesQueryFailureException() {
        super(ResultCode.EXERCISE_TYPES_QUERY_FAILURE, HttpStatus.BAD_REQUEST);
    }
}
