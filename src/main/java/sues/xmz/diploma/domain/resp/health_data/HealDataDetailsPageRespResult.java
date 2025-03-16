package sues.xmz.diploma.domain.resp.health_data;

import io.swagger.v3.oas.annotations.media.Schema;
import sues.xmz.diploma.common.domain.PageResponse;
import sues.xmz.diploma.common.domain.Result;
import sues.xmz.diploma.domain.dto.health_data.HealthDataDTO;

@Schema(description = "响应给前端的分页批量获取到的健康数据")
public class HealDataDetailsPageRespResult extends Result<PageResponse<HealthDataDTO>> {
    // 无需额外字段，仅用于明确SwaggerAPI文档中的响应数据及其类型
}
