package sues.xmz.diploma.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户信息表，存储用户的个人基本资料及注册信息
 */
@Schema(description = "用户信息表，存储用户的个人基本资料及注册信息")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "diploma_project_daily_exercise_health_management_system.users")
public class Users implements Serializable {
    /**
     * 用户ID
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "用户ID不能为null")
    private Integer userId;

    /**
     * 用户名
     */
    @TableField(value = "username")
    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(min = 1, max = 50, message = "账号名最大长度要小于 50")
    @NotBlank(message = "账号名不能为空")
    private String username;

    /**
     * 加密后的密码
     */
    @TableField(value = "password_hash")
    @Schema(description = "前端在注册时传递的是未加密的原始密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(min = 1, max = 50, message = "密码最大长度要小于 50")
    @NotBlank(message = "密码不能为空")
    private String passwordHash;

    /**
     * 用户的全名
     */
    @TableField(value = "full_name")
    @Schema(description = "用户的全名", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(min = 1, max = 50, message = "用户的全名不能为空字符，最大长度要小于 50")
    @Nullable
    private String fullName;

    /**
     * 用户性别：男、女
     */
    @TableField(value = "gender")
    @Schema(description = "用户性别：男、女", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Nullable
    @Pattern(regexp = "[男女]?", message = "只能输入”男”或”女”")
    private String gender;

    /**
     * 用户出生日期
     */
    @TableField(value = "birth_date")
    @Schema(description = "用户出生日期",
            pattern = "yyyy-MM-dd",
            example = "2025-01-01",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @Nullable
    @DateTimeFormat
    private Date birthDate;

    /**
     * 用户身高（单位：cm）
     */
    @TableField(value = "height")
    @Schema(description = "用户身高（单位：cm）", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Nullable
    private BigDecimal height;

    /**
     * 用户体重（单位：kg）
     */
    @TableField(value = "weight")
    @Schema(description = "用户体重（单位：kg）", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Nullable
    private BigDecimal weight;

    /**
     * 用户邮箱地址
     */
    @TableField(value = "email")
    @Schema(description = "用户邮箱地址", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Nullable
    @Email(message = "邮箱格式不正确")
    @Pattern(regexp = "^\\S.*", message = "邮箱的数据不能为空字符")
    private String email;

    /**
     * 用户电话号码
     */
    @TableField(value = "phone_number")
    @Schema(description = "用户电话号码", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Nullable
    @Size(min = 1, max = 20, message = "用户电话号码不能为空字符，最大长度要小于 20")
    private String phoneNumber;

    /**
     * 注册时间
     */
    @TableField(value = "created_at")
    @Schema(description = "注册时间")
    private Date createdAt;

    /**
     * 信息更新时间
     */
    @TableField(value = "updated_at")
    @Schema(description = "信息更新时间")
    private Date updatedAt;

    @Serial
    private static final long serialVersionUID = 1L;
}