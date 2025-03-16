package sues.xmz.diploma.domain.req.users;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用于传输用户ID、旧密码和新密码
 */
@Data
public class PasswordChangeReq implements Serializable {

    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为null")
    private Integer userId;

    @Schema(description = "旧密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(min = 1, max = 50, message = "密码最大长度要小于 50")
    @NotBlank(message = "密码不能为空")
    private String oldPassword;

    @Schema(description = "新密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(min = 1, max = 50, message = "密码最大长度要小于 50")
    @NotBlank(message = "密码不能为空")
    private String newPassword;

    @Serial
    private static final long serialVersionUID = 1L;
}