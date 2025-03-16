package sues.xmz.diploma.domain.req.diet_records;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Schema(description = "删除饮食记录的请求数据")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DietRecordsDeleteReq implements Serializable {

    /**
     * 饮食记录ID列表
     */
    @Schema(description = "饮食记录ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "饮食记录ID列表不能为空")
    private List<Integer> dietIdList;

    @Serial
    private static final long serialVersionUID = 1L;
}