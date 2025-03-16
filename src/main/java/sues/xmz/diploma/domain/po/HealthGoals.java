package sues.xmz.diploma.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户的健康目标表，记录用户的健康目标（如减脂、增肌等)
 */
@Schema(description = "用户的健康目标表，记录用户的健康目标（如减脂、增肌等)")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "diploma_project_daily_exercise_health_management_system.health_goals")
public class HealthGoals implements Serializable {
    /**
     * 健康目标计划ID
     */
    @TableId(value = "goal_id", type = IdType.AUTO)
    @Schema(description = "健康目标计划ID")
    @NotNull(message = "健康目标计划ID不能为null")
    private Integer goalId;

    /**
     * 用户ID
     */
    @TableField(value = "user_id")
    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为null")
    private Integer userId;

    /**
     * 目标计划
     */
    @TableField(value = "target_plan")
    @Schema(description = "目标计划")
    @NotBlank(message = "目标计划不能为空")
    private String targetPlan;

    /**
     * 目标达成日期
     */
    @TableField(value = "target_date")
    @Schema(description = "目标达成日期")
    @NotNull(message = "目标达成日期不能为null")
    private Date targetDate;

    /**
     * 是否完成计划 - 未完成：0，完成：1
     */
    @TableField(value = "is_finished")
    @Schema(description = "是否完成计划 - 未完成：0，完成：1")
    private Byte isFinished;

    /**
     * 是否放弃了计划 - 否：0，是：1
     */
    @TableField(value = "is_abandoned")
    @Schema(description = "是否放弃了计划 - 否：0，是：1")
    private Byte isAbandoned;

    /**
     * 目标创建时间
     */
    @TableField(value = "created_at")
    @Schema(description = "目标创建时间")
    private Date createdAt;

    /**
     * 目标更新时间
     */
    @TableField(value = "updated_at")
    @Schema(description = "目标更新时间")
    private Date updatedAt;

    @Serial
    private static final long serialVersionUID = 1L;
}