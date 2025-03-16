package sues.xmz.diploma.common.exception.diet_records;

import org.springframework.http.HttpStatus;
import sues.xmz.diploma.common.domain.ResultCode;
import sues.xmz.diploma.common.exception.BusinessException;

public class DietRecordsSaveFailureException extends BusinessException {
    public DietRecordsSaveFailureException() {
        super(ResultCode.DIET_RECORDS_SAVE_FAILURE, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
