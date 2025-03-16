package sues.xmz.diploma.domain.req.exercise_records;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Schema(description = "删除运动记录的请求参数")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseRecordDeleteReq implements Serializable {

    @Schema(description = "运动记录ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "运动记录ID列表不能为空")
    private List<Integer> exerciseRecordIdList;

    @Serial
    private static final long serialVersionUID = 1L;

}