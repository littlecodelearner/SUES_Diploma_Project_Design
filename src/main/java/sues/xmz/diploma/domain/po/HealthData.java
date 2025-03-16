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
 * 用户健康数据表，包括体重、BMI、心率等监测数据
 */
@Schema(description = "用户健康数据表，包括体重、BMI、心率等监测数据")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "diploma_project_daily_exercise_health_management_system.health_data")
public class HealthData implements Serializable {
    /**
     * 健康数据ID
     */
    @TableId(value = "health_data_id", type = IdType.AUTO)
    @Schema(description = "健康数据ID")
    @NotNull(message = "健康数据ID不能为null")
    private Integer healthDataId;

    /**
     * 用户ID
     */
    @TableField(value = "user_id")
    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为null")
    private Integer userId;

    /**
     * 当前心率（单位：bpm）
     */
    @TableField(value = "heart_rate")
    @Schema(description = "当前心率（单位：bpm）")
    private BigDecimal heartRate;

    /**
     * 体重（单位：kg）
     */
    @TableField(value = "weight")
    @Schema(description = "体重（单位：kg）")
    private BigDecimal weight;

    /**
     * 体质指数（BMI）
     */
    @TableField(value = "bmi")
    @Schema(description = "体质指数（BMI）")
    private BigDecimal bmi;

    /**
     * 健康数据记录日期
     */
    @TableField(value = "measurement_date")
    @Schema(description = "健康数据记录日期")
    @NotNull(message = "健康数据记录日期不能为null")
    private Date measurementDate;

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