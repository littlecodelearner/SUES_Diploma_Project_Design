package sues.xmz.diploma.domain.dto.diet_records;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Schema(description = "用户的饮食记录 - 包含的表数据：饮食记录表和食物表")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DietRecordsCalculationDTO implements Serializable {

    /**
     * 饮食记录ID
     */
    @Schema(description = "饮食记录ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "饮食记录ID不能为空")
    private Integer dietId;

    /**
     * 食物ID
     */
    @Schema(description = "食物ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "食物ID不能为空")
    private Integer foodId;

    /**
     * 记录日期
     */
    @TableField(value = "meal_time")
    @Schema(description = "记录饮食日期。举例：优先使用中国时区的日期格式`2023-10-05T14:30:00.123+08:00`",
            pattern=" yyyy-MM-dd'T'HH:mm:ss.SSSXXX ",
            example=" 2021-12-01T12:11:01.132 或 2021-12-01T12:11:01.132+08:00 ",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "进餐时间不能为空")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date mealTime;

    /**
     * 食用量
     */
    @Schema(description = "食用量（单位：g）", requiredMode = Schema.RequiredMode.REQUIRED)
    @DecimalMin(value = "0.00", message = "食用量必须大于等于0")
    @Positive(message = "食用量不能为空")
    private BigDecimal quantity;

    /**
     * 食物名称
     */
    @Schema(description = "食物名称（唯一）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "食物名称不能为空")
    private String foodName;

    /**
     * 食物的卡路里（单位：kcal）
     */
    @Schema(description = "每100g卡路里（单位：kcal）", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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