package sues.xmz.diploma.domain.req.diet_foods;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import sues.xmz.diploma.common.domain.PageQueryReq;

import java.io.Serial;
import java.io.Serializable;

@Schema(description="分页批量获取食物表数据的请求数据")
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class DietFoodsQueryReq extends PageQueryReq implements Serializable {

    /**
     * 食物名称
     */
    @Schema(description = "食物名称（唯一）。可为空", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Nullable
    private String foodName;

    /**
     * 食物分类
     */
    @Schema(description = "食物分类。可为空", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Nullable
    private String foodType;

    @Serial
    private static final long serialVersionUID = 1L;

}
