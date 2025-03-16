package sues.xmz.diploma.common.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Schema(description = "分页查询条件（多字段的排序功能让前端来实现）")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel. PACKAGE)
public class PageQueryReq implements Serializable {

    public static final Long DEFAULT_PAGE_NUM = 1L;
    public static final Long DEFAULT_PAGE_SIZE = 15L;

    @Schema(
            minimum = "1", description = "当前页码。默认从1开始，不能超过1000",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    /*  TODO:
         错误的使用：@Size(max = 1000, message = "页码不能超过1000")
         @Size 注解通常用于字符串、集合（如 List, Set, Map）、数组等类型的字段上，以限制这些类型的数据长度或大小。
         而 Long、Integer、BigDecimal、Double 等类型是一个数值类型，应该使用其他注解来进行验证，
         比如Long类型可以使用 @Min 和 @Max 来限制其取值范围。
   */
    @Min(value = 1, message = "页码不能小于1")
    @Max(value = 1000, message = "页码不能超过1000")
    @Nullable
    protected Long current = DEFAULT_PAGE_NUM;

    @Schema(
            minimum = "15", description = "每页展示的数据量。默认为15，不能超过50",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @Min(value = 15, message = "每页显示的数量不能小于15")
    @Max(value = 50, message = "每页显示的数量不能超过50")
    @Nullable
    protected Long size = DEFAULT_PAGE_SIZE;

    @Schema(description = "是否升序，默认true", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Nullable
    protected Boolean isAsc = true;

    @Schema(description = "开始日期时间。举例：优先使用中国时区的日期格式`2023-10-05T14:30:00.123+08:00`",
            pattern=" yyyy-MM-dd'T'HH:mm:ss.SSSXXX ",
            example=" 2021-12-01T12:11:01.132 或 2021-12-01T12:11:01.132+08:00 ",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @Nullable
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date startDateTime;

    @Schema(description = "结束日期时间。举例：优先使用中国时区的日期格式`2023-10-05T14:30:00.123+08:00`",
            pattern=" yyyy-MM-dd'T'HH:mm:ss.SSSXXX ",
            example=" 2021-12-01T12:11:01.132 或 2021-12-01T12:11:01.132+08:00 ",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @Nullable
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date endDateTime;

    @Serial
    private static final long serialVersionUID = 1L;
}
