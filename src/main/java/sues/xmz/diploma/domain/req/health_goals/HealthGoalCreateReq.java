package sues.xmz.diploma.domain.req.health_goals;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Schema(description = "创建健康目标的请求参数")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HealthGoalCreateReq implements Serializable {

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

    @Schema(description = "关联的运动类型ID列表", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Nullable
    private List<Integer> exerciseTypeIdList;
}