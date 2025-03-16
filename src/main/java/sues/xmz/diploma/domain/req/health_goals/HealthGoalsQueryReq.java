package sues.xmz.diploma.domain.req.health_goals;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import sues.xmz.diploma.common.domain.PageQueryReq;

import java.io.Serializable;

@Schema(description = "分页批量获取用户的所有健康目标计划的请求参数")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class HealthGoalsQueryReq extends PageQueryReq implements Serializable {

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "用户ID不能为空")
    private Integer userId;

}
