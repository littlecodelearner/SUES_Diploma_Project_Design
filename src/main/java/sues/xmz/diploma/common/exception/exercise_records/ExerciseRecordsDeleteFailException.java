package sues.xmz.diploma.common.exception.exercise_records;

import org.springframework.http.HttpStatus;
import sues.xmz.diploma.common.domain.ResultCode;
import sues.xmz.diploma.common.exception.BusinessException;

public class ExerciseRecordsDeleteFailException extends BusinessException {
    public ExerciseRecordsDeleteFailException() {
        super(ResultCode.EXERCISE_RECORDS_DELETE_FAIL, HttpStatus.BAD_REQUEST);
    }
}
