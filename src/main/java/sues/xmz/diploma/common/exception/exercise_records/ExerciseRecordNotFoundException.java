package sues.xmz.diploma.common.exception.exercise_records;

import org.springframework.http.HttpStatus;
import sues.xmz.diploma.common.domain.ResultCode;
import sues.xmz.diploma.common.exception.BusinessException;

public class ExerciseRecordNotFoundException extends BusinessException {
    public ExerciseRecordNotFoundException() {
        super(ResultCode.EXERCISE_RECORD_NOT_FOUND.getCode(), ResultCode.EXERCISE_RECORD_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND);
    }
}