package sues.xmz.diploma.domain.req.users;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import sues.xmz.diploma.common.domain.PageQueryReq;

import java.io.Serial;
import java.io.Serializable;

@Schema(description = "分页查询用户名的请求参数")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserQueryInBulkByPageReq extends PageQueryReq implements Serializable {

    @Schema(description = "用户名")
    @Nullable
    @Size(min = 1, max = 50, message = "账号名不能是空字符，且最大长度要小于 50")
    protected String username;

    @Serial
    private static final long serialVersionUID = 1L;
}
