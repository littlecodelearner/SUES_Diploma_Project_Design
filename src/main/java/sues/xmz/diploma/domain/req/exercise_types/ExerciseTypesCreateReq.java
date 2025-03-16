package sues.xmz.diploma.domain.req.exercise_types;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 运动类型表，定义所有可能的运动类型
 */
@Schema(description = "添加运动类型的请求数据")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseTypesCreateReq implements Serializable {

    /**
     * 运动类型名称
     */
    @Schema(description = "需要添加的运动类型名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(min = 1, message = "列表中的运动类型数量必须大于0")
    @NotEmpty(message = "列表中的运动类型不能为空")
    private List<String> exerciseNameList;

    @Serial
    private static final long serialVersionUID = 1L;
}