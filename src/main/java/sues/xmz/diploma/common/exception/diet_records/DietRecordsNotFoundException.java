package sues.xmz.diploma.common.exception.diet_records;

import org.springframework.http.HttpStatus;
import sues.xmz.diploma.common.domain.ResultCode;
import sues.xmz.diploma.common.exception.BusinessException;

public class DietRecordsNotFoundException extends BusinessException {
    public DietRecordsNotFoundException() {
        super(ResultCode.DIET_RECORDS_NOT_FOUND.getCode(), ResultCode.DIET_RECORDS_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND);
    }
}
