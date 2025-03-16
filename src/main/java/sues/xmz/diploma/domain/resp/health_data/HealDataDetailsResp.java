package sues.xmz.diploma.domain.resp.health_data;

import io.swagger.v3.oas.annotations.media.Schema;
import sues.xmz.diploma.common.domain.Result;
import sues.xmz.diploma.domain.po.HealthData;

import java.util.List;

@Schema(description = "响应给前端的健康数据")
public class HealDataDetailsResp extends Result<List<HealthData>> {
    // 无需额外字段，仅用于明确SwaggerAPI文档中的响应数据及其类型
}
