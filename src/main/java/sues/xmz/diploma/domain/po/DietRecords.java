package sues.xmz.diploma.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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

/**
 * 用户的饮食记录表，记录每日的饮食情况
 */
@Schema(description = "用户的饮食记录表，记录每日的饮食情况。")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "diploma_project_daily_exercise_health_management_system.diet_records")
public class DietRecords implements Serializable {
    
    /**
     * 饮食记录ID
     */
    @TableId(value = "diet_id", type = IdType.AUTO)
    @Schema(description = "饮食记录ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "饮食记录ID不能为null")
    private Integer dietId;

    /**
     * 用户ID
     */
    @TableField(value = "user_id")
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "用户ID不能为null")
    private Integer userId;


    /**
     * 进餐类型
     */
    @TableField(value = "meal_type")
    @Schema(description = "早餐、午餐、下午茶、晚餐、零食......",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 10, message = "进餐类型最大长度要小于 10")
    @NotBlank(message = "进餐类型不能为空")
    private String mealType;

    /**
     * 记录日期
     */
    @TableField(value = "meal_time")
    @Schema(description = "记录日期 - 日期格式举例：2021-12-01T12:11:01.132 或 2021-12-01T12:11:01.132+08:00",
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
    @TableField(value = "meal_note")
    @Schema(description = "饮食记录的备注或笔记", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 500, message = "笔记字数规定不超过500字")
    @Nullable
    private String mealNote;


    /**
     * 进餐地点
     */
    @TableField(value = "meal_place")
    @Schema(description = "进餐地点", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 50, message = "进餐地点字数不超过50字")
    @Nullable
    private String mealPlace;

    /**
     * 记录创建时间
     */
    @TableField(value = "created_at")
    @Schema(description = "记录创建时间")
    private Date createdAt;

    /**
     * 记录更新时间
     */
    @TableField(value = "updated_at")
    @Schema(description = "记录更新时间")
    private Date updatedAt;

    @Serial
    private static final long serialVersionUID = 1L;
}