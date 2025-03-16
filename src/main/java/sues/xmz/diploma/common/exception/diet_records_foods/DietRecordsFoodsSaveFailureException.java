package sues.xmz.diploma.common.exception.diet_records_foods;

import org.springframework.http.HttpStatus;
import sues.xmz.diploma.common.domain.ResultCode;
import sues.xmz.diploma.common.exception.BusinessException;

public class DietRecordsFoodsSaveFailureException extends BusinessException {
    public DietRecordsFoodsSaveFailureException() {
        super(ResultCode.DIET_RECORDS_FOODS_SAVE_FAILURE, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
