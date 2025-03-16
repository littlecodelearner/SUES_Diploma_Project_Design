package sues.xmz.diploma.domain.resp.diet_records;

import io.swagger.v3.oas.annotations.media.Schema;
import sues.xmz.diploma.common.domain.PageResponse;
import sues.xmz.diploma.common.domain.Result;
import sues.xmz.diploma.domain.dto.diet_records.DietRecordsDTO;

@Schema(description = "响应的按时间段进行分页批量获取饮食记录的整体数据")
public class DietRecordsPageRespResult extends Result<PageResponse<DietRecordsDTO>> {
    // 无需额外字段，仅用于明确SwaggerAPI文档中的响应数据及其类型
}