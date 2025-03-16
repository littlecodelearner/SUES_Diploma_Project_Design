package sues.xmz.diploma.domain.req.diet_foods;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Schema(description="批量删除食物表数据的请求数据")
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class DietFoodsDeleteReq implements Serializable {

    /**
     * 食物ID
     */
    @Schema(description = "食物ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "食物ID列表不能为空")
    List<Integer> foodIdList;

    @Serial
    private static final long serialVersionUID = 1L;

}
