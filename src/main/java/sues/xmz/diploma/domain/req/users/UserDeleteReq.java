package sues.xmz.diploma.domain.req.users;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Schema(description = "删除用户的请求参数")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDeleteReq implements Serializable {

    /**
     * 用户名
     */
    @Schema(description = "用户ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "用户ID列表不能为空")
    List<Integer> userIdList;

    @Serial
    private static final long serialVersionUID = 1L;
}