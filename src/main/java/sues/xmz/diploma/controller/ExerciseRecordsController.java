package sues.xmz.diploma.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import sues.xmz.diploma.common.domain.EmptyPageRespResult;
import sues.xmz.diploma.common.domain.PageResponse;
import sues.xmz.diploma.common.domain.Result;
import sues.xmz.diploma.common.exception.exercise_records.ExerciseRecordsDeleteFailException;
import sues.xmz.diploma.domain.dto.exercise_records_types.ExerciseRecordsDTO;
import sues.xmz.diploma.domain.req.exercise_records.ExerciseRecordDeleteReq;
import sues.xmz.diploma.domain.req.exercise_records.ExerciseRecordUpdateReq;
import sues.xmz.diploma.domain.req.exercise_records.ExerciseRecordsCreateReq;
import sues.xmz.diploma.domain.req.exercise_records.ExerciseRecordsQueryReq;
import sues.xmz.diploma.domain.resp.exercise_records.ExerciseRecordsPageRespResult;
import sues.xmz.diploma.service.ExerciseRecordsService;

import java.util.List;

@Tag(name = "06运动记录管理模块", description = "运动记录相关API")
@RestController
@RequestMapping("/exerciseRecords")
public class ExerciseRecordsController {

    @Resource
    private ExerciseRecordsService exerciseRecordsService;

    @ApiOperationSupport(order = 1)
    @PostMapping
    @Operation(
            tags = {".05登录后运动记录操作"},
            summary = "用户批量创建新的运动记录_前端已实现",
            description = "批量创建新的运动记录",
            responses = {
                    @ApiResponse(responseCode = "200", description = "新的运动记录保存成功"),
                    @ApiResponse(responseCode = "1011", description = "新的运动记录保存失败"),
                    @ApiResponse(responseCode = "1012", description = "运动记录与类型之间的联系保存失败"),
                    @ApiResponse(responseCode = "unknown", description = "服务端出现问题"),
            }
    )
    public Result<?> saveExerciseRecordsInBatch(@RequestBody @Valid List<ExerciseRecordsCreateReq> exerciseRecordsCreateReqList) {
        exerciseRecordsService.saveExerciseRecordInBatch(exerciseRecordsCreateReqList);
        return Result.success("新的运动记录保存成功");
    }

    @ApiOperationSupport(order = 2)
    @PutMapping
    @Operation(
            tags = {".05登录后运动记录操作"},
            summary = "用户批量更新运动记录_前端已实现",
            description = "根据运动记录ID，批量更新数据",
            responses = {
                    @ApiResponse(responseCode = "200", description = "运动记录更新成功"),
                    @ApiResponse(responseCode = "1010", description = "运动记录没找到"),
                    @ApiResponse(responseCode = "1013", description = "运动记录与类型之间的联系更新失败"),
                    @ApiResponse(responseCode = "unknown", description = "服务端出现问题"),
            }
    )
    public Result<?> updateExerciseRecord(@RequestBody @Valid List<ExerciseRecordUpdateReq> exerciseRecordUpdateReqList) {
        exerciseRecordsService.updateExerciseRecordInBatch(exerciseRecordUpdateReqList);
        return Result.success("运动记录更新成功");
    }

    @ApiOperationSupport(order = 3)
    @DeleteMapping
    @Operation(
            tags = {".05登录后运动记录操作"},
            summary = "用户批量删除运动记录_前端已实现",
            description = "根据运动记录ID，批量删除数据",
            responses = {
                    @ApiResponse(responseCode = "200", description = "运动记录删除成功"),
                    @ApiResponse(responseCode = "1030", description = "运动记录删除失败，删除的运动记录已经不存在"),
                    @ApiResponse(responseCode = "unknown", description = "服务端出现问题"),
            }
    )
    public Result<?> deleteExerciseRecordInBulk(@RequestBody @Valid ExerciseRecordDeleteReq exerciseRecordDeleteReq) {
        boolean success = exerciseRecordsService.removeBatchByIds(exerciseRecordDeleteReq.getExerciseRecordIdList());
        if (!success) {
            throw new ExerciseRecordsDeleteFailException();
        }
        return Result.success("运动记录删除成功");
    }

    @ApiOperationSupport(order = 4)
    @PostMapping("/listByPage")
    @Operation(
            tags = {".05登录后运动记录操作"},
            summary = "用户根据时间段来分页批量获取运动记录_前端已实现",
            description = "根据时间段来分页批量获取运动记录",
            responses = {
                    @ApiResponse(responseCode = "200", description = "获取用户的运动记录成功",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ExerciseRecordsPageRespResult.class))
                    ),
                    @ApiResponse(responseCode = "1010", description = "运动记录没找到",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = EmptyPageRespResult.class))
                    ),
                    @ApiResponse(responseCode = "unknown", description = "服务端出现问题"),
            }
    )
    public Result<?> listPaginatedExerciseRecordsByTimeRange(@RequestBody @Valid ExerciseRecordsQueryReq exerciseRecordsQueryReq) {
        try {
            PageResponse<ExerciseRecordsDTO> pageResponse = exerciseRecordsService.listPaginatedExerciseRecordsByTimeRange(exerciseRecordsQueryReq);
            if (pageResponse.getTotal() < 1){
                return Result.success(PageResponse.empty());
            }
            return Result.success(pageResponse);
        } catch (Exception e) {
            throw new RuntimeException("服务端出现问题：" + e, e);
        }
    }
}