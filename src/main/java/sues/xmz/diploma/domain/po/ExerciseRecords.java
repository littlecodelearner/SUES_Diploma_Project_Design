package sues.xmz.diploma.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 运动记录表，记录每次用户的运动数据
 */
@Schema(description = "运动记录表，记录每次用户的运动数据")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "diploma_project_daily_exercise_health_management_system.exercise_records")
public class ExerciseRecords implements Serializable {
    /**
     * 运动记录ID
     */
    @TableId(value = "exercise_record_id", type = IdType.AUTO)
    @Schema(description = "运动记录ID")
    @NotNull(message = "运动记录ID不能为null")
    private Integer exerciseRecordId;

    /**
     * 用户ID
     */
    @TableField(value = "user_id")
    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为null")
    private Integer userId;

    /**
     * 运动时长（单位：分钟）
     */
    @TableField(value = "duration")
    @Schema(description = "运动时长（单位：分钟）")
    private Integer duration;

    /**
     * 运动距离（单位：km）
     */
    @TableField(value = "distance")
    @Schema(description = "运动距离（单位：km）")
    private BigDecimal distance;

    /**
     * 消耗的卡路里（单位：kcal）
     */
    @TableField(value = "calories_burned")
    @Schema(description = "消耗的卡路里（单位：kcal）")
    private BigDecimal caloriesBurned;

    /**
     * 运动中的平均心率（单位：bpm）
     */
    @TableField(value = "heart_rate")
    @Schema(description = "运动中的平均心率（单位：bpm）")
    private BigDecimal heartRate;

    /**
     * 运动记录的备注、随笔和详细内容
     */
    @TableField(value = "exercise_note")
    @Schema(description = "运动记录的备注、随笔和详细内容")
    private String exerciseNote;

    /**
     * 运动日期
     */
    @TableField(value = "exercise_date")
    @Schema(description = "运动日期")
    @NotNull(message = "运动日期不能为null")
    private Date exerciseDate;

    /**
     * 记录创建时间
     */
    @TableField(value = "created_at")
    @Schema(description = "记录创建时间")
    private Date createdAt;

    /**
     * 记录更新时间
     */
    @TableField(value = "updated_at")
    @Schema(description = "记录更新时间")
    private Date updatedAt;

    @Serial
    private static final long serialVersionUID = 1L;
}