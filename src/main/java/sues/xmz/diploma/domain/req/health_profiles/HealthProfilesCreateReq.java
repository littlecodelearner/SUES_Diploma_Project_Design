package sues.xmz.diploma.domain.req.health_profiles;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 创建用户的健康档案
 */
@Schema(description = "创建用户健康档案的请求数据")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HealthProfilesCreateReq implements Serializable {

    /**
     * 用户ID
     */
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "用户ID不能为null")
    private Integer userId;

    /**
     * 用户的疾病史
     */
    @Schema(description = "用户的疾病史", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Nullable
    private String medicalHistory;

    /**
     * 用户的过敏史
     */
    @Schema(description = "用户的过敏史", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Nullable
    private String allergyHistory;

    /**
     * 用户的运动习惯
     */
    @Schema(description = "用户的运动习惯", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Nullable
    private String exerciseHabits;

    /**
     * 用户设定的健康目标
     */
    @Schema(description = "用户设定的健康目标", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Nullable
    private String healthGoals;

    @Serial
    private static final long serialVersionUID = 1L;
}