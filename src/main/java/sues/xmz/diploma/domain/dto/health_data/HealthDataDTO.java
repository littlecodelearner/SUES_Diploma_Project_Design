package sues.xmz.diploma.domain.dto.health_data;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "用户健康数据相关信息")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HealthDataDTO implements Serializable {

    /**
     * 健康数据ID
     */
    @Schema(description = "健康数据ID")
    private Integer healthDataId;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Integer userId;


    /**
     * 身高（单位：cm）
     */
    @Schema(description = "身高（单位：cm） - 通过联表查询获得用户的身高")
    private BigDecimal height;

    /**
     * 体重（单位：kg）
     */
    @Schema(description = "体重（单位：kg）")
    private BigDecimal weight;

    /**
     * 体质指数（BMI）
     */
    @Schema(description = "体质指数（BMI）- 偏瘦：<= 18.4；正常：18.5 ~ 23.9；过重：24.0 ~ 27.9；肥胖：>= 28.0")
    private BigDecimal bmi;

    /**
     * 当前心率（单位：bpm）
     */
    @Schema(description = "当前心率（单位：bpm）")
    private BigDecimal heartRate;

    /**
     * 健康数据记录日期
     */
    @Schema(description = "健康数据记录日期")
    private Date measurementDate;

    @Serial
    private static final long serialVersionUID = 1L;
}