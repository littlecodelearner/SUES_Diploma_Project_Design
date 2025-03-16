package sues.xmz.diploma.domain.dto.health_data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class HealthDataTrendDTO {

    @Schema(description = "日期列表")
    private List<Date> measurementDates;

    @Schema(description = "心率趋势")
    private List<BigDecimal> heartRates;

    @Schema(description = "体重趋势")
    private List<BigDecimal> weights;

    @Schema(description = "BMI趋势")
    private List<BigDecimal> bmis;
}