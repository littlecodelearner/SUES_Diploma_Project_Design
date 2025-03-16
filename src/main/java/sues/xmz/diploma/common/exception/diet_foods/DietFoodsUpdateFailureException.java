package sues.xmz.diploma.common.exception.diet_foods;

import org.springframework.http.HttpStatus;
import sues.xmz.diploma.common.domain.ResultCode;
import sues.xmz.diploma.common.exception.BusinessException;

public class DietFoodsUpdateFailureException extends BusinessException {
    public DietFoodsUpdateFailureException() {
        super(ResultCode.DIET_FOODS_UPDATE_FAILURE, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
