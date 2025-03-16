package sues.xmz.diploma.domain.dto.health_goals;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import sues.xmz.diploma.domain.po.ExerciseTypes;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Schema(description = "获取健康目标计划的请求参数")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HealthGoalsDetailsDTO implements Serializable {

    @Schema(description = "健康目标计划ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "健康目标计划ID不能为null")
    private Integer goalId;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "用户ID不能为空")
    private Integer userId;

    @Schema(description = "目标计划", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "目标计划不能为空")
    private String targetPlan;

    @Schema(description = "目标达成日期。举例：优先使用中国时区的日期格式`2023-10-05T14:30:00.123+08:00`",
            pattern = "yyyy-MM-dd",
            example = "2025-01-01",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull(message = "目标达成日期不能为null")
    private Date targetDate;

    /**
     * 是否完成计划 - 未完成：0，完成：1
     */
    @Schema(description = "是否完成计划 - 未完成：0，完成：1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否完成计划的参数不能为空")
    private Byte isFinished;

    /**
     * 是否放弃了计划 - 否：0，是：1
     */
    @Schema(description = "是否放弃了计划 - 否：0，是：1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否放弃了计划的参数不能为空")
    private Byte isAbandoned;


    @Schema(description = "关联的运动类型数据列表", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Nullable
    private List<ExerciseTypes> exerciseTypesList;

    @Serial
    private static final long serialVersionUID = 1L;
}
