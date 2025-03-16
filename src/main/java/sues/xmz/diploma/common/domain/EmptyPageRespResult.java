package sues.xmz.diploma.common.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "获取到了空的分页响应数据")
@Data
@EqualsAndHashCode(callSuper = true)
public class EmptyPageRespResult extends Result<PageResponse<?>> {
    // 无需额外字段，仅用于明确SwaggerAPI文档中的响应数据及其类型
}
