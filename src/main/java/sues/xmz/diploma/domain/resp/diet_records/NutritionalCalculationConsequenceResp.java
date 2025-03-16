package sues.xmz.diploma.domain.resp.diet_records;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sues.xmz.diploma.domain.dto.diet_records.DietRecordsCalculationDTO;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Schema(description = "营养摄入计算结果")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NutritionalCalculationConsequenceResp implements Serializable {

    @Schema(description = "总热量（kcal）")
    private BigDecimal totalCalories;

    @Schema(description = "总蛋白质（g）")
    private BigDecimal totalProtein;

    @Schema(description = "总脂肪（g）")
    private BigDecimal totalFat;

    @Schema(description = "总碳水化合物（g）")
    private BigDecimal totalCarbohydrates;

    @Schema(description = "总水分（ml）")
    private BigDecimal totalWater;

    @Schema(description = "摄入食物的相关信息")
    private List<DietRecordsCalculationDTO> dietRecordsList;

    @Serial
    private static final long serialVersionUID = 1L;
}