package sues.xmz.diploma.domain.resp.diet_records;

import io.swagger.v3.oas.annotations.media.Schema;
import sues.xmz.diploma.common.domain.Result;

@Schema(description = "进行某时间段内的营养摄入计算结果的响应数据")
public class NutritionalCalulationConsequenceRespResult extends Result<NutritionalCalculationConsequenceResp> {
    // 无需额外字段，仅用于明确SwaggerAPI文档中的响应数据及其类型
}
