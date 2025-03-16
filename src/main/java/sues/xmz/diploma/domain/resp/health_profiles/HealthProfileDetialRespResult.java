package sues.xmz.diploma.domain.resp.health_profiles;

import io.swagger.v3.oas.annotations.media.Schema;
import sues.xmz.diploma.common.domain.Result;
import sues.xmz.diploma.domain.dto.health_profiles.HealthProfileDTO;

@Schema(description = "响应给前端的用户健康档案数据")
public class HealthProfileDetialRespResult extends Result<HealthProfileDTO> {
    // 无需额外字段，仅用于明确SwaggerAPI文档中的响应数据及其类型

}
