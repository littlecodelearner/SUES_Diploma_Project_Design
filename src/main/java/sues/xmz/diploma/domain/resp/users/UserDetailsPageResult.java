package sues.xmz.diploma.domain.resp.users;

import io.swagger.v3.oas.annotations.media.Schema;
import sues.xmz.diploma.common.domain.PageResponse;
import sues.xmz.diploma.common.domain.Result;
import sues.xmz.diploma.domain.dto.users.UserDetailDTO;

@Schema(description = "响应的分页批量获取所有用户详细信息的整体数据")
public class UserDetailsPageResult extends Result<PageResponse<UserDetailDTO>> {
    // 无需额外字段，仅用于明确SwaggerAPI文档中的响应数据及其类型
}