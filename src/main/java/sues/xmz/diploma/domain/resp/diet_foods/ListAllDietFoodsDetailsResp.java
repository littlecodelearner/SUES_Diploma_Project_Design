package sues.xmz.diploma.domain.resp.diet_foods;

import io.swagger.v3.oas.annotations.media.Schema;
import sues.xmz.diploma.common.domain.Result;
import sues.xmz.diploma.domain.po.DietFoods;

import java.util.List;

@Schema(description = "获取所有食物信息的响应数据")
public class ListAllDietFoodsDetailsResp extends Result<List<DietFoods>> {
    // 无需额外字段，仅用于明确SwaggerAPI文档中的响应数据及其类型
}
