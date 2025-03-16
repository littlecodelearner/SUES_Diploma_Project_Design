package sues.xmz.diploma.common.exception.exercise_records_types;

import org.springframework.http.HttpStatus;
import sues.xmz.diploma.common.domain.ResultCode;
import sues.xmz.diploma.common.exception.BusinessException;

public class ExerciseRecordsTypesUpdateFailException extends BusinessException {
    public ExerciseRecordsTypesUpdateFailException() {
        super(ResultCode.EXERCISE_RECORD_TYPE_UPDATE_FAIL.getCode(),ResultCode.EXERCISE_RECORD_TYPE_UPDATE_FAIL.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
