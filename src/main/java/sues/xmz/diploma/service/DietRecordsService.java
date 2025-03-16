package sues.xmz.diploma.service;

import com.baomidou.mybatisplus.extension.service.IService;
import sues.xmz.diploma.common.domain.PageResponse;
import sues.xmz.diploma.domain.dto.diet_records.DietRecordsDTO;
import sues.xmz.diploma.domain.po.DietRecords;
import sues.xmz.diploma.domain.req.diet_records.DietRecordsCalculationReq;
import sues.xmz.diploma.domain.req.diet_records.DietRecordsCreateReq;
import sues.xmz.diploma.domain.req.diet_records.DietRecordsQueryReq;
import sues.xmz.diploma.domain.req.diet_records.DietRecordsUpdateReq;
import sues.xmz.diploma.domain.resp.diet_records.NutritionalCalculationConsequenceResp;

import java.util.List;

public interface DietRecordsService extends IService<DietRecords>{

    void addDietRecordsInBulk(List<DietRecordsCreateReq> dietRecordsCreateReqList);

    void updateDietRecordsInBulk(List<DietRecordsUpdateReq> dietRecordsUpdateReqList);

    PageResponse<DietRecordsDTO> listPaginatedDietRecordsByTimeRange(DietRecordsQueryReq dietRecordQueryDTO);

    NutritionalCalculationConsequenceResp calculateNutritionIntake(DietRecordsCalculationReq dietRecordsCalculationReq);
}
