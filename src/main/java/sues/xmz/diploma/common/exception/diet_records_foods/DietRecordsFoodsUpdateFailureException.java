package sues.xmz.diploma.common.exception.diet_records_foods;

import org.springframework.http.HttpStatus;
import sues.xmz.diploma.common.domain.ResultCode;
import sues.xmz.diploma.common.exception.BusinessException;

public class DietRecordsFoodsUpdateFailureException extends BusinessException {
    public DietRecordsFoodsUpdateFailureException() {
        super(ResultCode.DIET_RECORDS_FOODS_UPDATE_FAILURE, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
