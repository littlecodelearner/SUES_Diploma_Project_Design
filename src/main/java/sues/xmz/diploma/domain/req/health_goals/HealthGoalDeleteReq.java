package sues.xmz.diploma.domain.req.health_goals;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Schema(description = "删除健康目标计划数据的请求参数")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HealthGoalDeleteReq implements Serializable {

    @Schema(description = "目标ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "目标ID列表不能为空")
    private List<Integer> goalIdList;

    @Serial
    private static final long serialVersionUID = 1L;

}