package sues.xmz.diploma.common.exception.exercise_records_types;

import org.springframework.http.HttpStatus;
import sues.xmz.diploma.common.domain.ResultCode;
import sues.xmz.diploma.common.exception.BusinessException;

public class ExerciseRecordsTypesSaveFailException extends BusinessException {
    public ExerciseRecordsTypesSaveFailException() {
        super(ResultCode.EXERCISE_RECORD_TYPE_SAVE_FAIL.getCode(),ResultCode.EXERCISE_RECORD_TYPE_SAVE_FAIL.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
