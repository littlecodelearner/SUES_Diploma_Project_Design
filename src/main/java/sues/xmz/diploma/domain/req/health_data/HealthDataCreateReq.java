package sues.xmz.diploma.domain.req.health_data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Schema(description = "创建健康数据的请求参数")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthDataCreateReq implements Serializable {

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "用户ID不能为空")
    private Integer userId;

    @Schema(description = "当前心率（单位：bpm）", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Nullable
    private BigDecimal heartRate;

    @Schema(description = "体重（单位：kg）", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Positive(message = "体重不能为空")
    private BigDecimal weight;

    @Schema(description = "健康数据记录日期（每天记录一次）。举例：优先使用中国时区的日期格式`2023-10-05T14:30:00.123+08:00`",
            pattern=" yyyy-MM-dd'T'HH:mm:ss.SSSXXX ",
            example=" 2021-12-01T12:11:01.132 或 2021-12-01T12:11:01.132+08:00 ",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull(message = "健康数据记录日期不能为空")
    private Date measurementDate;
}