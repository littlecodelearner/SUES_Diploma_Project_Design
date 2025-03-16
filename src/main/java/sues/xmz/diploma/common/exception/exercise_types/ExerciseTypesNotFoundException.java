package sues.xmz.diploma.common.exception.exercise_types;

import org.springframework.http.HttpStatus;
import sues.xmz.diploma.common.domain.ResultCode;
import sues.xmz.diploma.common.exception.BusinessException;

public class ExerciseTypesNotFoundException extends BusinessException {
    public ExerciseTypesNotFoundException() {
        super(ResultCode.EXERCISE_TYPES_NOT_FOUND.getCode(), ResultCode.EXERCISE_TYPES_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND);
    }
}
