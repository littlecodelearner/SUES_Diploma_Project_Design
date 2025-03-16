package sues.xmz.diploma.domain.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 饮食记录与食物的多对多关联表
 * </p>
 *
 * @author 徐铭泽
 * @since 2025-03-06
 */
@Schema(description = "饮食记录表与食物表的多对多关联表")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("diploma_project_daily_exercise_health_management_system.diet_records_foods")
public class DietRecordsFoods implements Serializable {

    @TableField("diet_id")
    @Schema(description = "饮食记录ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "饮食记录ID不能为空")
    private Integer dietId;

    @TableField("food_id")
    @Schema(description = "食物ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "食物ID不能为空")
    private Integer foodId;

    @TableField(value = "quantity")
    @Schema(description = "食用量（单位：g）", requiredMode = Schema.RequiredMode.REQUIRED)
    @DecimalMin(value = "0.00", message = "食用量必须大于等于0")
    @Positive(message = "食用量不能为空")
    private BigDecimal quantity;

    @Serial
    private static final long serialVersionUID = 1L;

}
