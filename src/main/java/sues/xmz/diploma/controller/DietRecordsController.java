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
import sues.xmz.diploma.common.domain.PageResponse;
import sues.xmz.diploma.common.domain.Result;
import sues.xmz.diploma.common.exception.diet_records.DietRecordsDeleteFailureException;
import sues.xmz.diploma.domain.dto.diet_records.DietRecordsDTO;
import sues.xmz.diploma.domain.po.DietRecords;
import sues.xmz.diploma.domain.req.diet_records.*;
import sues.xmz.diploma.domain.resp.diet_records.AllMealTypeRespResult;
import sues.xmz.diploma.domain.resp.diet_records.DietRecordsPageRespResult;
import sues.xmz.diploma.domain.resp.diet_records.NutritionalCalulationConsequenceRespResult;
import sues.xmz.diploma.domain.resp.exercise_records.ExerciseRecordsPageRespResult;
import sues.xmz.diploma.service.DietRecordsService;

import java.util.List;
import java.util.Map;

@Tag(name = "04.2饮食记录管理", description = "饮食记录相关API")
@RequestMapping("/dietRecords")
@RestController
public class DietRecordsController {

    @Resource
    private DietRecordsService dietRecordsService;

    @ApiOperationSupport(order = 1)
    @Operation(
            summary = "批量添加饮食记录_前端已实现",
            description = "用户批量添加新的饮食记录",
            tags = {".04登录后饮食记录操作"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "新的饮食记录添加成功"),
                    @ApiResponse(responseCode = "1018", description = "新的饮食记录添加失败"),
                    @ApiResponse(responseCode = "1019", description = "新的【饮食记录表与食物表之间的联系】的数据添加失败"),
            }
    )
    @PostMapping
    public Result<?> addDietRecordsInBulk(@RequestBody @Valid List<DietRecordsCreateReq> dietRecordsCreateReqList) {
        dietRecordsService.addDietRecordsInBulk(dietRecordsCreateReqList);
        return Result.success("新的饮食记录添加成功");
    }

    @ApiOperationSupport(order = 2)
    @Operation(
            summary = "批量修改现有的饮食记录_前端已实现",
            description = "用户批量修改现有的饮食记录",
            tags = {".04登录后饮食记录操作"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "饮食记录修改成功"),
                    @ApiResponse(responseCode = "others", description = "数据库操作可能出现问题"),
                    @ApiResponse(responseCode = "1020", description = "饮食记录修改失败"),
                    @ApiResponse(responseCode = "1021", description = "【饮食记录表与食物表之间的联系】的数据修改失败"),
            }
    )
    @PutMapping
    public Result<?> updateDietRecordsInBulk(@RequestBody @Valid List<DietRecordsUpdateReq> dietRecordsUpdateReqList) {
        dietRecordsService.updateDietRecordsInBulk(dietRecordsUpdateReqList);
        return Result.success("饮食记录修改成功");
    }

    @ApiOperationSupport(order = 3)
    @Operation(
            summary = "批量删除饮食记录_前端已实现",
            description = "根据饮食记录ID，来批量删除饮食记录",
            tags = {".04登录后饮食记录操作"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "指定ID的饮食记录删除成功"),
                    @ApiResponse(responseCode = "1022", description = "指定ID的饮食记录删除失败"),
            }
    )
    @DeleteMapping
    public Result<?> deleteDietRecordInBulk(@RequestBody @Valid DietRecordsDeleteReq dietRecordsDeleteReq) {
        boolean success = dietRecordsService.removeBatchByIds(dietRecordsDeleteReq.getDietIdList());
        if (!success) {
            throw new DietRecordsDeleteFailureException();
        }
        return Result.success("指定ID的饮食记录删除成功");
    }

    @ApiOperationSupport(order = 4)
    @Operation(
            summary = "根据时间段分页批量查看饮食记录_前端已实现",
            description = "根据用户ID和日期范围相关参数分页查询饮食记录",
            tags = {".04登录后饮食记录操作"},
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "用户分页批量查看饮食记录成功",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DietRecordsPageRespResult.class)
                            )
                    ),
                    @ApiResponse(responseCode = "others", description = "没找到用户分页批量查看饮食记录",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ExerciseRecordsPageRespResult.class))
                    ),
            }
    )
    @PostMapping("/listByPage")
    public Result<?> listPaginatedDietRecordsByTimeRange(@RequestBody @Valid DietRecordsQueryReq dietRecordQueryReq) {
        PageResponse<DietRecordsDTO> pageResponse = dietRecordsService.listPaginatedDietRecordsByTimeRange(dietRecordQueryReq);
        if (pageResponse.getTotal() < 1) {
            return Result.success(PageResponse.empty());
        }
        return Result.success(pageResponse);
    }

    @ApiOperationSupport(order = 5)
    @Operation(
            summary = "获取所有的进餐类型_前端已实现",
            tags = {".04登录后饮食记录操作"},
            description = "获取所有的进餐类型",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "获取所有的进餐类型成功",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = AllMealTypeRespResult.class,
                                            example = """
                                                    {
                                                      "code": 200,
                                                      "message": "成功",
                                                      "data": {
                                                        "mealTypeList": [
                                                          "早餐",
                                                          "零食",
                                                          "午餐",
                                                          "晚餐",
                                                          "加餐",
                                                          "下午茶"
                                                        ]
                                                      }
                                                    }
                                                    """
                                    )
                            )
                    ),
            }
    )
    @GetMapping("/mealTypes")
    public Result<?> getMealTypes() {
        List<String> mealTypeList = dietRecordsService.lambdaQuery()
                .select(DietRecords::getMealType)
                .list()
                .stream()
                .map(DietRecords::getMealType)
                .distinct()
                .toList();
        if (mealTypeList.isEmpty()) {
            return Result.success("未找到相关进餐类型");
        }
        return Result.success(Map.of("mealTypeList", mealTypeList));
    }

    @ApiOperationSupport(order = 6)
    @Operation(
            summary = "进行某时间段内的营养摄入计算_前端已实现",
            description = "在某时间段内，对于用户摄入的营养进行计算，并返回总结",
            tags = {".04登录后饮食记录操作"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "获取某时间段内，用户摄入的营养总和成功",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = NutritionalCalulationConsequenceRespResult.class))
                    ),
                    @ApiResponse(responseCode = "others", description = "获取某时间段内，用户摄入的营养总和失败"),
            }
    )
    @PostMapping("/calculateIntakeByTimeRange")
    public Result<?> calculateNutritionIntake(@RequestBody @Valid DietRecordsCalculationReq dietRecordsCalculationReq) {
        try {
            return Result.success(dietRecordsService.calculateNutritionIntake(dietRecordsCalculationReq));
        } catch (Exception e) {
            throw new RuntimeException("获取某时间段内，用户摄入的营养总和失败：" + e, e);
        }
    }

}