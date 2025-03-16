package sues.xmz.diploma.domain.resp.exercise_records;

import io.swagger.v3.oas.annotations.media.Schema;
import sues.xmz.diploma.common.domain.PageResponse;
import sues.xmz.diploma.common.domain.Result;
import sues.xmz.diploma.domain.dto.exercise_records_types.ExerciseRecordsDTO;

@Schema(description = "响应给前端的根据时间段来分页批量获取的运动记录数据")
public class ExerciseRecordsPageRespResult extends Result<PageResponse<ExerciseRecordsDTO>> {
    // 无需额外字段，仅用于明确SwaggerAPI文档中的响应数据及其类型
}