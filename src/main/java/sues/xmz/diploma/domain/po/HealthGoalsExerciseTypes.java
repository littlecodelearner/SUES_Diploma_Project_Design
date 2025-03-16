package sues.xmz.diploma.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 健康目标计划和运动类型之间的多对多关系表。目标计划与运动类型的多对多关系表，表示一个目标计划可能关联多个运动类型，而一个运动类型也可能对应多个目标计划
 */
@Schema(description = "健康目标计划和运动类型之间的多对多关系表。目标计划与运动类型的多对多关系表，表示一个目标计划可能关联多个运动类型，而一个运动类型也可能对应多个目标计划")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "health_goals_exercise_types")
public class HealthGoalsExerciseTypes implements Serializable {

    /**
     * 健康目标计划ID
     */
//    @TableField(value = "goal_id")
    @Schema(description = "健康目标计划ID")
    @NotNull(message = "健康目标计划ID不能为null")
    private Integer goalId;

    /**
     * 运动类型ID
     */
//    @TableField(value = "exercise_type_id")
    @Schema(description = "运动类型ID")
    @NotNull(message = "运动类型ID不能为null")
    private Integer exerciseTypeId;

    @Serial
    private static final long serialVersionUID = 1L;
}