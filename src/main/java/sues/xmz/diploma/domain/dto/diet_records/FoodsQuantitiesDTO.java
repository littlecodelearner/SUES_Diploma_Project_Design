package sues.xmz.diploma.domain.dto.diet_records;

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

@Schema(description = "每个食物的食用量（单位：g）")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodsQuantitiesDTO implements Serializable {

    /**
     * 食物ID列表
     */
    @Schema(description = "食物ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "食物ID不能为空")
    private Integer foodId;

    /**
     * 食用量
     */
    @Schema(description = "食用量（单位：g）", requiredMode = Schema.RequiredMode.REQUIRED)
    @DecimalMin(value = "0.00", message = "食用量必须大于等于0")
    @Positive(message = "食用量不能为空")
    private BigDecimal quantity;

    @Serial
    private static final long serialVersionUID = 1L;
}
