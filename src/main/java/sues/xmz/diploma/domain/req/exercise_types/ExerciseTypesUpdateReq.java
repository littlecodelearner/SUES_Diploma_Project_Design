package sues.xmz.diploma.domain.req.exercise_types;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Schema(description = "需要更新的运动类型")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseTypesUpdateReq implements Serializable {

    @Schema(description = "需要更新的运动类型ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "运动类型ID不能为空")
    private Integer exerciseTypeId;

    @Schema(description = "修改后的运动类型名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(min = 1, message = "列表中的运动类型数量必须大于0")
    @NotBlank(message = "列表中的运动类型不能为空")
    private String newExerciseTypeName;

    @Serial
    private static final long serialVersionUID = 1L;
}