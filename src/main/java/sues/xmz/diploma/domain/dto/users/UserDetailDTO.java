package sues.xmz.diploma.domain.dto.users;

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
 * 获取用户信息
 */
@Schema(description = "获取用户信息")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailDTO implements Serializable {
    /**
     * 用户ID
     */
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "用户ID不能为null")
    private Integer userId;

    /**
     * 用户名
     */
    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(min = 1, max = 50, message = "账号名最大长度要小于 50")
    @NotBlank(message = "账号名不能为空")
    private String username;

    /**
     * 用户的全名
     */
    @Schema(description = "用户的全名", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(min = 1, max = 50, message = "用户的全名不能为空字符，最大长度要小于 50")
    @Nullable
    private String fullName;

    /**
     * 用户性别：男、女
     */
    @Schema(description = "用户性别：男、女", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Nullable
    @Pattern(regexp = "[男女]?", message = "只能输入”男”或”女”")
    private String gender;

    /**
     * 用户出生日期
     */
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
    @Schema(description = "用户身高（单位：cm）", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Nullable
    private BigDecimal height;

    /**
     * 用户体重（单位：kg）
     */
    @Schema(description = "用户体重（单位：kg）", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Nullable
    private BigDecimal weight;

    /**
     * 用户邮箱地址
     */
    @Schema(description = "用户邮箱地址", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Nullable
    @Email(message = "邮箱格式不正确")
    @Pattern(regexp = "^\\S.*", message = "邮箱的数据不能为空字符")
    private String email;

    /**
     * 用户电话号码
     */
    @Schema(description = "用户电话号码", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Nullable
    @Size(min = 1, max = 20, message = "用户电话号码不能为空字符，最大长度要小于 20")
    private String phoneNumber;

    /**
     * 注册时间
     */
    @Schema(description = "注册时间", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Nullable
    private Date createdAt;

    /**
     * 信息更新时间
     */
    @Schema(description = "信息更新时间", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Nullable
    private Date updatedAt;

    @Serial
    private static final long serialVersionUID = 1L;
}