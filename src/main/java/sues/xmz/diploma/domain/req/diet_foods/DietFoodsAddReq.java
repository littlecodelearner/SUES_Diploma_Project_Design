package sues.xmz.diploma.domain.req.diet_foods;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Schema(description="批量增加食物表的请求数据")
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class DietFoodsAddReq implements Serializable {

    /**
     * 食物名称
     */
    @Schema(description = "食物名称（唯一）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "食物名称不能为空")
    private String foodName;

    /**
     * 食物的卡路里（单位：kcal）
     */
    @Schema(description = "每100g卡路里（单位：kcal）", requiredMode = Schema.RequiredMode.REQUIRED)
    @DecimalMin(value = "0.00", message = "卡路里不能为负数")
    @NotNull(message = "卡路里不能为空")
    private BigDecimal calories;

    /**
     * 蛋白质含量（单位：g）
     */
    @Schema(description = "每100g蛋白质含量（单位：g）", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Nullable
    private BigDecimal protein;

    /**
     * 脂肪含量（单位：g）
     */
    @Schema(description = "每100g脂肪含量（单位：g）", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Nullable
    private BigDecimal fat;

    /**
     * 碳水化合物含量（单位：g）
     */
    @Schema(description = "每100g碳水化合物含量（单位：g）", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Nullable
    private BigDecimal carbohydrates;

    /**
     * 水分含量（单位：ml）
     */
    @Schema(description = "每100g水分含量（单位：ml）", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Nullable
    private BigDecimal water;

    /**
     * 食物分类
     */
    @Schema(description = "食物分类", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "食物分类不能为空")
    private String foodType;

    @Serial
    private static final long serialVersionUID = 1L;

}
