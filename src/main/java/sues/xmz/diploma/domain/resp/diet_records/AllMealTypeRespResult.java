package sues.xmz.diploma.domain.resp.diet_records;

import io.swagger.v3.oas.annotations.media.Schema;
import sues.xmz.diploma.common.domain.Result;

import java.util.List;
import java.util.Map;

@Schema(description = "所有的进餐类型数据")
public class AllMealTypeRespResult extends Result<Map<String, List<String>>> {
    // 无需额外字段，仅用于明确SwaggerAPI文档中的响应数据及其类型
}