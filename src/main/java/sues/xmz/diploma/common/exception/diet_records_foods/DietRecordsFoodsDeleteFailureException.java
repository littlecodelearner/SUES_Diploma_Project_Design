package sues.xmz.diploma.common.exception.diet_records_foods;

import org.springframework.http.HttpStatus;
import sues.xmz.diploma.common.domain.ResultCode;
import sues.xmz.diploma.common.exception.BusinessException;

public class DietRecordsFoodsDeleteFailureException extends BusinessException {
    public DietRecordsFoodsDeleteFailureException() {
        super(ResultCode.DIET_RECORDS_FOODS_DELETE_FAILURE, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
