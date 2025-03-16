package sues.xmz.diploma.domain.req.diet_records;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import sues.xmz.diploma.common.domain.PageQueryReq;

import java.io.Serial;
import java.io.Serializable;

@Schema(description = "进行饮食记录计算的请求数据")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class DietRecordsCalculationReq extends PageQueryReq implements Serializable {

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "用户ID不能为空")
    private Integer userId;

    @Serial
    private static final long serialVersionUID = 1L;
}
