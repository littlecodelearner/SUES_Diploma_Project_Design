package sues.xmz.diploma.common.exception.diet_records;

import org.springframework.http.HttpStatus;
import sues.xmz.diploma.common.domain.ResultCode;
import sues.xmz.diploma.common.exception.BusinessException;

public class DietRecordsUpdateFailureException extends BusinessException {
    public DietRecordsUpdateFailureException() {
        super(ResultCode.DIET_RECORDS_UPDATE_FAILURE.getCode(), ResultCode.DIET_RECORDS_UPDATE_FAILURE.getMessage(), HttpStatus.NOT_FOUND);
    }
}
