package sues.xmz.diploma.common.exception.diet_records;

import org.springframework.http.HttpStatus;
import sues.xmz.diploma.common.domain.ResultCode;
import sues.xmz.diploma.common.exception.BusinessException;

public class DietRecordsDeleteFailureException extends BusinessException {
    public DietRecordsDeleteFailureException() {
        super(ResultCode.DIET_RECORDS_DELETE_FAILURE.getCode(), ResultCode.DIET_RECORDS_DELETE_FAILURE.getMessage(), HttpStatus.NOT_FOUND);
    }
}
