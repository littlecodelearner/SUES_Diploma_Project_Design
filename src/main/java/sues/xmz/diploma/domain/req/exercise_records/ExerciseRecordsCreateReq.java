package sues.xmz.diploma.domain.req.exercise_records;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Schema(description = "创建运动记录的信息")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseRecordsCreateReq implements Serializable {

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "用户ID不能为空")
    private Integer userId;

    @Schema(description = "多个运动类型ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Nullable
    private List<Integer> exerciseTypeIdList;

    @Schema(description = "运动日期。举例：优先使用中国时区的日期格式`2023-10-05T14:30:00.123+08:00`",
            pattern = "yyyy-MM-dd",
            example = "2025-01-01",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "运动日期不能为null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date exerciseDate;

    @Schema(description = "运动记录的备注、随笔和详细内容", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Nullable
    private String exerciseNote;

    @Schema(description = "运动时长（单位：分钟）", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Nullable
    private Integer duration;

    @Schema(description = "运动距离（单位：km）", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Nullable
    private BigDecimal distance;

    @Schema(description = "消耗的卡路里（单位：kcal）", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Nullable
    private BigDecimal caloriesBurned;

    @Schema(description = "平均心率（单位：bpm）", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Nullable
    private BigDecimal heartRate;

    @Serial
    private static final long serialVersionUID = 1L;
}