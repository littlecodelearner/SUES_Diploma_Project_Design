package sues.xmz.diploma.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.apache.ibatis.executor.BatchResult;
import org.springframework.web.bind.annotation.*;
import sues.xmz.diploma.common.domain.EmptyPageRespResult;
import sues.xmz.diploma.common.domain.PageResponse;
import sues.xmz.diploma.common.domain.Result;
import sues.xmz.diploma.common.exception.exercise_types.ExerciseTypesCreateFailureException;
import sues.xmz.diploma.common.exception.exercise_types.ExerciseTypesDeleteFailureException;
import sues.xmz.diploma.common.exception.exercise_types.ExerciseTypesUpdateFailureException;
import sues.xmz.diploma.domain.po.ExerciseTypes;
import sues.xmz.diploma.domain.req.exercise_types.ExerciseTypesCreateReq;
import sues.xmz.diploma.domain.req.exercise_types.ExerciseTypesDeleteReq;
import sues.xmz.diploma.domain.req.exercise_types.ExerciseTypesQueryReq;
import sues.xmz.diploma.domain.req.exercise_types.ExerciseTypesUpdateReq;
import sues.xmz.diploma.domain.resp.exercise_types.AllExerciseTypesResp;
import sues.xmz.diploma.domain.resp.exercise_types.ListPagedExerciseTypesRespResult;
import sues.xmz.diploma.service.ExerciseTypesService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "05运动类型管理", description = "运动类型管理相关API")
@RequestMapping("/exerciseTypes")
@RestController
public class ExerciseTypesController {

    @Resource
    private ExerciseTypesService exerciseTypesService;

    @ApiOperationSupport(order = 20)
    @Operation(
            tags = {"_管理员操作模块"},
            summary = "01运动类型-批量添加运动类型_前端已实现",
            description = "批量添加新的运动类型",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "新的运动类型添加成功",
                            content = @Content(
                                    schema = @Schema(
                                            implementation = Result.class,
                                            example = """
                                                    {
                                                      "code": 200,
                                                      "message": "成功",
                                                      "data": {
                                                        "添加新的运动类型成功": [
                                                          "高强度波比跳",
                                                          "混氧游泳"
                                                        ]
                                                      }
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "1028", description = "新的运动类型添加失败")
            }
    )
    @PostMapping
    public Result<?> addExerciseTypesInBatch(@RequestBody @Valid ExerciseTypesCreateReq exerciseTypesCreateReq) {
        List<BatchResult> batchResultList = exerciseTypesService.addExerciseTypesInBatch(exerciseTypesCreateReq);

        if (batchResultList.get(0).getUpdateCounts().length != exerciseTypesCreateReq.getExerciseNameList().size()) {
            throw new ExerciseTypesCreateFailureException();
        }

        /*
        TODO：使用以下语法来初始化Map对象，虽然简洁，但不推荐在生产环境中大量使用，因为它会在堆内存中创建额外的匿名类实例，可能导致内存泄漏问题。
            return Result.success(new HashMap<String, Object>() {
                {put("添加新的运动类型成功", exerciseTypesCreateReq.getExerciseNameList());}
            });
        */
        /*
        TODO：注意：Map.of 创建的是不可变的 Map，如果需要可变的 Map，可以将其转换为其他类型的 Map。举例：
         Map<String, Object> mutableMap = new HashMap<>(Map.of(
                        "添加新的运动类型成功", exerciseTypesCreateReq.getExerciseNameList()
                ));
        */
        return Result.success(Map.of(
                "添加新的运动类型成功", exerciseTypesCreateReq.getExerciseNameList()
        ));
    }

    @ApiOperationSupport(order = 21)
    @Operation(
            tags = {"_管理员操作模块"},
            summary = "01运动类型-批量修改运动类型_前端已实现",
            description = "批量修改运动类型",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "运动类型修改成功",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = Result.class,
                                            example = """
                                                    {
                                                      "code": 200,
                                                      "message": "成功",
                                                      "data": {
                                                        "修改运动类型成功": [
                                                          {
                                                            "exerciseTypeId": 198,
                                                            "newExerciseTypeName": "门球"
                                                          },
                                                          {
                                                            "exerciseTypeId": 161,
                                                            "newExerciseTypeName": "SUP激情冲浪"
                                                          }
                                                        ]
                                                      }
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "1009", description = "请求数据中没找到运动类型等数据"),
                    @ApiResponse(responseCode = "1029", description = "运动类型修改失败"),
            }
    )
    @PutMapping
    public Result<?> updateExerciseTypesInBatch(@RequestBody @Valid List<ExerciseTypesUpdateReq> exerciseTypesUpdateReqList) {
        List<BatchResult> batchResultList = exerciseTypesService.updateExerciseTypesInBatch(exerciseTypesUpdateReqList);

        if (batchResultList.get(0).getUpdateCounts().length != exerciseTypesUpdateReqList.size()) {
            throw new ExerciseTypesUpdateFailureException();
        }
        return Result.success(new HashMap<>(Map.of("修改运动类型成功", exerciseTypesUpdateReqList)));
    }

    @ApiOperationSupport(order = 22)
    @Operation(
            tags = {"_管理员操作模块"},
            summary = "01运动类型-批量删除运动类型_前端已实现",
            description = "根据运动类型ID，来批量删除运动类型",
            responses = {
                    @ApiResponse(responseCode = "200", description = "运动类型删除成功",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = Result.class,
                                            example = """
                                                    {
                                                      "code": 200,
                                                      "message": "成功",
                                                      "data": {
                                                        "删除运动类型成功": [
                                                          132,
                                                          196
                                                        ]
                                                      }
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "1023", description = "运动类型删除失败"),
            }
    )
    @DeleteMapping
    public Result<?> deleteExerciseTypeInBatch(@RequestBody @Valid ExerciseTypesDeleteReq exerciseTypesDeleteReq) {
        boolean success = exerciseTypesService.removeBatchByIds(exerciseTypesDeleteReq.getExerciseTypeIdList());

        if (!success) {
            throw new ExerciseTypesDeleteFailureException();
        }
        return Result.success(Map.of("删除运动类型成功", exerciseTypesDeleteReq.getExerciseTypeIdList()));
    }

    @ApiOperationSupport(order = 23)
    @Operation(
            summary = "01运动类型-批量分页查看运动类型_前端已实现",
            tags = {"_管理员操作模块"},
            description = "批量分页查看运动类型",
            responses = {
                    @ApiResponse(responseCode = "200", description = "分页查看运动类型成功",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ListPagedExerciseTypesRespResult.class)
                            )
                    ),
                    @ApiResponse(responseCode = "others", description = "查看运动类型失败",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = EmptyPageRespResult.class))
                    )
            }
    )
    @PostMapping("/listByPage")
    public Result<?> listExerciseTypesByPage(@RequestBody @Valid ExerciseTypesQueryReq exerciseTypesQueryReq) {
        PageResponse<ExerciseTypes> pageResponse = exerciseTypesService.listExerciseTypesByPage(
                exerciseTypesQueryReq.getCurrent(),
                exerciseTypesQueryReq.getSize(),
                exerciseTypesQueryReq.getName()
        );
        if (pageResponse.getTotal() < 1) {
            return Result.success(PageResponse.empty());
        }
        return Result.success(pageResponse);
    }

    @ApiOperationSupport(order = 24)
    @Operation(
            summary = "01运动类型-获取所有运动类型_前端已实现",
            tags = {"_管理员操作模块"},
            description = "获取所有运动类型",
            responses = {
                    @ApiResponse(responseCode = "200", description = "获取所有运动类型成功",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AllExerciseTypesResp.class)
                            )
                    ),
                    @ApiResponse(responseCode = "others", description = "获取所有运动类型失败")
            }
    )
    @GetMapping
    public Result<?> getAllExerciseTypesByPage() {
        return Result.success(exerciseTypesService.list());
    }

}
