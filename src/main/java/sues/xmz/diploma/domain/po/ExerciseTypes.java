package sues.xmz.diploma.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 运动类型表，定义所有可能的运动类型
 */
@Schema(description = "运动类型表，定义所有可能的运动类型")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "diploma_project_daily_exercise_health_management_system.exercise_types")
public class ExerciseTypes implements Serializable {

    public ExerciseTypes(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    /**
     * 运动类型ID
     */
    @TableId(value = "exercise_type_id", type = IdType.AUTO)
    @Schema(description = "运动类型ID - 对于删除操作：可以只添加运动类型的名字，不添加添加运动类型ID")
    @NotNull(message = "运动类型ID不能为空")
    private Integer exerciseTypeId;

    /**
     * 运动类型名称
     */
    @TableField(value = "exercise_name")
    @Schema(description = "运动类型名称")
    @Size(max = 50, message = "运动类型名称最大长度要小于 50")
    @NotBlank(message = "运动类型名称不能为空")
    private String exerciseName;

    @Serial
    private static final long serialVersionUID = 1L;
}