package sues.xmz.diploma.domain.req.users;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户登录信息
 */
@Schema(description = "用户登录信息")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginReq implements Serializable {

    /**
     * 用户名
     */
    @Schema(description = "用户名（账号）", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(min = 1, max = 50, message = "账号名最大长度要小于 50")
    @NotBlank(message = "账号名不能为空")
    private String username;

    /**
     * 加密后的密码
     */
    @Schema(description = "原始密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(min = 1, max = 50, message = "密码最大长度要小于 50")
    @NotBlank(message = "密码不能为空")
    private String passwordHash;

    @Serial
    private static final long serialVersionUID = 1L;
}