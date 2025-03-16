package sues.xmz.diploma.domain.resp.diet_foods;

import io.swagger.v3.oas.annotations.media.Schema;
import sues.xmz.diploma.common.domain.PageResponse;
import sues.xmz.diploma.common.domain.Result;
import sues.xmz.diploma.domain.dto.diet_foods.DietFoodsDTO;

@Schema(description = "分页批量获取到食物信息的响应数据")
public class ListPagedDietFoodsRespResult extends Result<PageResponse<DietFoodsDTO>> {
    // 无需额外字段，仅用于明确SwaggerAPI文档中的响应数据及其类型
}
