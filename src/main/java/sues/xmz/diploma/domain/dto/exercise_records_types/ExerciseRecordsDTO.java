package sues.xmz.diploma.domain.dto.exercise_records_types;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sues.xmz.diploma.domain.po.ExerciseTypes;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Schema(description = "响应给运动记录及其类型的分页数据")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseRecordsDTO {

    /**
     * 运动记录ID
     */
    @Schema(description = "运动记录ID")
    private Integer exerciseRecordId;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Integer userId;

    /**
     * 运动时长（单位：分钟）
     */
    @Schema(description = "运动时长（单位：分钟）")
    private Integer duration;

    /**
     * 运动距离（单位：km）
     */
    @Schema(description = "运动距离（单位：km）")
    private BigDecimal distance;

    /**
     * 消耗的卡路里（单位：kcal）
     */
    @Schema(description = "消耗的卡路里（单位：kcal）")
    private BigDecimal caloriesBurned;

    /**
     * 运动中的平均心率（单位：bpm）
     */
    @Schema(description = "运动中的平均心率（单位：bpm）")
    private BigDecimal heartRate;

    /**
     * 运动记录的备注、随笔和详细内容
     */
    @Schema(description = "运动记录的备注、随笔和详细内容")
    private String exerciseNote;

    /**
     * 运动日期
     */
    @Schema(description = "运动日期")
    private Date exerciseDate;

    @Schema(description = "运动类型列表", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Nullable
    private List<ExerciseTypes> exerciseTypesList;

}
