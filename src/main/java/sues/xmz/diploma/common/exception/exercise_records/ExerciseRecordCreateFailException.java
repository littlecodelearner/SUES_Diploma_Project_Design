package sues.xmz.diploma.common.exception.exercise_records;

import org.springframework.http.HttpStatus;
import sues.xmz.diploma.common.domain.ResultCode;
import sues.xmz.diploma.common.exception.BusinessException;

public class ExerciseRecordCreateFailException extends BusinessException {
    public ExerciseRecordCreateFailException() {
        super(ResultCode.EXERCISE_RECORD_CREATE_FAIL.getCode(), ResultCode.EXERCISE_RECORD_CREATE_FAIL.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
