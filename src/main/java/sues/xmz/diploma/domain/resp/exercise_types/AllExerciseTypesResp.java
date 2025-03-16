package sues.xmz.diploma.domain.resp.exercise_types;

import io.swagger.v3.oas.annotations.media.Schema;
import sues.xmz.diploma.common.domain.Result;
import sues.xmz.diploma.domain.po.ExerciseTypes;

import java.util.List;

@Schema(description = "获取到所有运动类型的响应数据")
public class AllExerciseTypesResp extends Result<List<ExerciseTypes>> {
    // 无需额外字段，仅用于明确SwaggerAPI文档中的响应数据及其类型

}
