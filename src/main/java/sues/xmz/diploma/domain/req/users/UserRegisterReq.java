package sues.xmz.diploma.domain.req.users;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户注册信息
 */
@Schema(description = "用户注册信息")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterReq implements Serializable {

    /**
     * 用户名
     */
    @Schema(description = "注册账号（用户名）", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(min = 1, max = 50, message = "账号名最大长度要小于 50")
    @NotBlank(message = "账号名不能为空")
    private String username;

    /**
     * 加密之前的密码
     */
    @Schema(description = "原始密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(min = 1, max = 50, message = "密码最大长度要小于 50")
    @NotBlank(message = "密码不能为空")
    private String passwordHash;

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
     * <p>
     * TODO
     *  举例：优先使用中国时区的日期格式 - `2023-10-05T14:30:00.123+08:00`
     *  <p>
     *  支持的日期格式：
     *  <p>
     *  "yyyy-MM-dd"格式："2025-01-01"
     *  <p>
     *  "yyyy-MM-dd'T'HH:mm:ss.SSSX"格式："2023-10-05T14:30:00.123+08:00"
     *  <p>
     *  "yyyy-MM-dd'T'HH:mm:ss.SSS"格式："2023-10-05T14:30:00.123"
     *  <p>
     *  "EEE, dd MMM yyyy HH:mm:ss zzz"格式："Thu, 05 Oct 2023 14:30:00 CST"
     *  <p>
     *  其它的日期格式均不支持
     */
    @Schema(description = "用户出生日期。举例：优先使用中国时区的日期格式`2023-10-05T14:30:00.123+08:00`",
            pattern = "yyyy-MM-dd",
            example = "2025-01-01",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @Nullable
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
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

    @Serial
    private static final long serialVersionUID = 1L;
}