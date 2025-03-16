package sues.xmz.diploma.domain.req.exercise_types;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import sues.xmz.diploma.common.domain.PageQueryReq;

import java.io.Serial;
import java.io.Serializable;

@Schema(description = "查询运动类型列表的请求参数")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseTypesQueryReq extends PageQueryReq implements Serializable {

    @Schema(description = "要进行搜索的运动类型名称", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Nullable
    private String name;

    @Serial
    private static final long serialVersionUID = 1L;
}
