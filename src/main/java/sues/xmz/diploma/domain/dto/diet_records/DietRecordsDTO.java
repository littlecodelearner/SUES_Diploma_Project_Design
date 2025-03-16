package sues.xmz.diploma.domain.dto.diet_records;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Schema(description = "用户的饮食记录 - 包含的表数据：饮食记录表和食物表")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DietRecordsDTO implements Serializable {

    /**
     * 饮食记录ID
     */
    @Schema(description = "饮食记录ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "饮食记录ID不能为空")
    private Integer dietId;

    @Schema(description = "用户每餐的饮食信息列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "用户每餐的饮食信息列表不能为空")
    private List<FoodsDetailsDTO> foodsDetailsDTOList;

    /**
     * 进餐类型
     */
    @Schema(description = "早餐、午餐、下午茶、晚餐、零食......",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 10, message = "进餐类型最大长度要小于 10")
    @NotBlank(message = "进餐类型不能为空")
    private String mealType;

    /**
     * 记录日期
     */
    @Schema(description = "记录饮食日期。举例：优先使用中国时区的日期格式`2023-10-05T14:30:00.123+08:00`",
            pattern=" yyyy-MM-dd'T'HH:mm:ss.SSSXXX ",
            example=" 2021-12-01T12:11:01.132 或 2021-12-01T12:11:01.132+08:00 ",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotEmpty(message = "进餐时间不能为空")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date mealTime;

    /**
     * 进餐笔记
     */
    @Schema(description = "饮食记录的备注或笔记", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 500, message = "笔记字数规定不超过500字")
    @Nullable
    private String mealNote;

    /**
     * 进餐地点
     */
    @Schema(description = "进餐地点", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 50, message = "进餐地点字数不超过50字")
    @Nullable
    private String mealPlace;

    @Serial
    private static final long serialVersionUID = 1L;
}