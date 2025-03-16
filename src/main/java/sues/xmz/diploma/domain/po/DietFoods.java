package sues.xmz.diploma.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 标准化食物营养成分表
 * </p>
 *
 * @author 徐铭泽
 * @since 2025-03-06
 */
@Schema(description="食物表：包含标准化食物营养成分数据及其分类")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("diploma_project_daily_exercise_health_management_system.diet_foods")
public class DietFoods implements Serializable {

    /**
     * 食物ID
     */
    @TableId(value = "food_id", type = IdType.AUTO)
    @Schema(description = "食物ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "食物ID不能为空")
    private Integer foodId;

    /**
     * 食物名称
     */
    @TableField(value = "food_name")
    @Schema(description = "食物名称（唯一）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "食物名称不能为空")
    private String foodName;

    /**
     * 食物的卡路里（单位：kcal）
     */
    @TableField(value = "calories")
    @Schema(description = "每100g卡路里（单位：kcal）", requiredMode = Schema.RequiredMode.REQUIRED)
    @DecimalMin(value = "0.00", message = "卡路里不能为负数")
    @NotNull(message = "卡路里不能为空")
    private BigDecimal calories;

    /**
     * 蛋白质含量（单位：g）
     */
    @TableField(value = "protein")
    @Schema(description = "每100g蛋白质含量（单位：g）", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Nullable
    private BigDecimal protein;

    /**
     * 脂肪含量（单位：g）
     */
    @TableField(value = "fat")
    @Schema(description = "每100g脂肪含量（单位：g）", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Nullable
    private BigDecimal fat;

    /**
     * 碳水化合物含量（单位：g）
     */
    @TableField(value = "carbohydrates")
    @Schema(description = "每100g碳水化合物含量（单位：g）", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Nullable
    private BigDecimal carbohydrates;

    /**
     * 水分含量（单位：ml）
     */
    @TableField(value = "water")
    @Schema(description = "每100g水分含量（单位：ml）", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Nullable
    private BigDecimal water;

    /**
     * 食物分类
     */
    @TableField(value = "food_type")
    @Schema(description = "食物分类", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "食物分类不能为空")
    private String foodType;

    @TableField(value = "created_at")
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @TableField(value = "updated_at")
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    @Serial
    private static final long serialVersionUID = 1L;

}
