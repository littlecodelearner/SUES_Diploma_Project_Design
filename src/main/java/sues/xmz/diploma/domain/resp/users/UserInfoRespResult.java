package sues.xmz.diploma.domain.resp.users;

import io.swagger.v3.oas.annotations.media.Schema;
import sues.xmz.diploma.common.domain.Result;
@Schema(description = "用于返回用户个人信息到前端的响应数据")
public class UserInfoRespResult extends Result<UserInfoResp> {
    // 无需额外字段，仅用于明确SwaggerAPI文档中的响应数据及其类型
}
