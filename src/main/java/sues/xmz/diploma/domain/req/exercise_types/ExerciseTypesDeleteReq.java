package sues.xmz.diploma.domain.req.exercise_types;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;


@Schema(description = "需要删除的运动类型")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseTypesDeleteReq implements Serializable {

    @Schema(description = "需要删除的的运动类型ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "运动类型ID列表不能为空")
    private List<Integer> exerciseTypeIdList;

    @Serial
    private static final long serialVersionUID = 1L;
}