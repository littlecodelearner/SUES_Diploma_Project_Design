package sues.xmz.diploma.domain.req.health_data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Schema(description = "删除健康数据的请求参数")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthDataDeleteReq implements Serializable {

    @Schema(description = "健康数据ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "健康数据ID列表不能为空")
    private List<Integer> healthDataIdList;

    @Serial
    private static final long serialVersionUID = 1L;
}