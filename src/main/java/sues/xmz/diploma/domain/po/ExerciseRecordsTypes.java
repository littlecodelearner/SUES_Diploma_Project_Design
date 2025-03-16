package sues.xmz.diploma.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 运动记录和运动类型之间的多对多关系表。一个运动记录可能关联多个运动类型，而一个运动类型也可能对应多个运动记录
 */
@Schema(description = "运动记录和运动类型之间的多对多关系表。一个运动记录可能关联多个运动类型，而一个运动类型也可能对应多个运动记录")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "diploma_project_daily_exercise_health_management_system.exercise_records_types")
public class ExerciseRecordsTypes implements Serializable {

    /**
     * 运动记录ID
     */
    @TableId(value = "exercise_record_id", type = IdType.NONE)
    @Schema(description = "运动记录ID")
    @NotNull(message = "运动记录ID不能为null")
    private Integer exerciseRecordId;

    /**
     * 运动类型ID
     */
//    @TableId(value = "exercise_type_id", type = IdType.NONE)
    @Schema(description = "运动类型ID")
    @NotNull(message = "运动类型ID不能为null")
    private Integer exerciseTypeId;

    @Serial
    private static final long serialVersionUID = 1L;
}