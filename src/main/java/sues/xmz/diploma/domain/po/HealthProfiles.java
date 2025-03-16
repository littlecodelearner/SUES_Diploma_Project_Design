package sues.xmz.diploma.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户的健康档案表，记录疾病史、过敏史及健康目标
 */
@Schema(description = "用户的健康档案表，记录疾病史、过敏史及健康目标")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "diploma_project_daily_exercise_health_management_system.health_profiles")
public class HealthProfiles implements Serializable {

    /**
     * 健康档案ID
     */
    @TableId(value = "profile_id", type = IdType.AUTO)
    @Schema(description = "健康档案ID")
    @NotNull(message = "健康档案ID不能为null")
    private Integer profileId;

    /**
     * 用户ID
     */
    @TableField(value = "user_id")
    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为null")
    private Integer userId;

    /**
     * 用户的疾病史
     */
    @TableField(value = "medical_history")
    @Schema(description = "用户的疾病史")
    private String medicalHistory;

    /**
     * 用户的过敏史
     */
    @TableField(value = "allergy_history")
    @Schema(description = "用户的过敏史")
    private String allergyHistory;

    /**
     * 用户的运动习惯
     */
    @TableField(value = "exercise_habits")
    @Schema(description = "用户的运动习惯")
    private String exerciseHabits;

    /**
     * 用户设定的健康目标
     */
    @TableField(value = "health_goals")
    @Schema(description = "用户设定的健康目标")
    private String healthGoals;

    /**
     * 档案创建时间
     */
    @TableField(value = "created_at")
    @Schema(description = "档案创建时间")
    private Date createdAt;

    /**
     * 档案更新时间
     */
    @TableField(value = "updated_at")
    @Schema(description = "档案更新时间")
    private Date updatedAt;

    @Serial
    private static final long serialVersionUID = 1L;
}