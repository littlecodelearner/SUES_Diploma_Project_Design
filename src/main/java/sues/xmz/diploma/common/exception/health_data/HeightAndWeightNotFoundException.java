package sues.xmz.diploma.common.exception.health_data;

import org.springframework.http.HttpStatus;
import sues.xmz.diploma.common.domain.ResultCode;
import sues.xmz.diploma.common.exception.BusinessException;

public class HeightAndWeightNotFoundException extends BusinessException {
    public HeightAndWeightNotFoundException() {
        super(ResultCode.HEIGHT_AND_WEIGHT_NOT_FOUND_FAILURE, HttpStatus.NOT_FOUND);
    }
}
