package sues.xmz.diploma.common.exception.exercise_records_types;

import org.springframework.http.HttpStatus;
import sues.xmz.diploma.common.domain.ResultCode;
import sues.xmz.diploma.common.exception.BusinessException;

public class ExerciseRecordsTypesDeleteFailException extends BusinessException {
    public ExerciseRecordsTypesDeleteFailException() {
        super(ResultCode.EXERCISE_RECORD_TYPE_DELETE_FAIL, HttpStatus.BAD_REQUEST);
    }
}
