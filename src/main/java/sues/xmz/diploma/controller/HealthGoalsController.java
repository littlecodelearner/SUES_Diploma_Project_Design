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
import sues.xmz.diploma.common.exception.health_goals.HealthGoalDeleteFailureException;
import sues.xmz.diploma.domain.dto.health_goals.HealthGoalsDetailsDTO;
import sues.xmz.diploma.domain.req.health_goals.HealthGoalCreateReq;
import sues.xmz.diploma.domain.req.health_goals.HealthGoalDeleteReq;
import sues.xmz.diploma.domain.req.health_goals.HealthGoalUpdateReq;
import sues.xmz.diploma.domain.req.health_goals.HealthGoalsQueryReq;
import sues.xmz.diploma.domain.resp.health_goals.HealthGoalsDetailsPageRespResult;
import sues.xmz.diploma.service.HealthGoalsService;

import java.util.List;

@RestController
@RequestMapping("/healthGoals")
@Tag(name = "07健康目标管理模块", description = "健康目标相关API")
public class HealthGoalsController {

    @Resource
    private HealthGoalsService healthGoalsService;

    @ApiOperationSupport(order = 1)
    @PostMapping
    @Operation(
            summary = "批量创建健康目标_前端已实现",
            description = "批量创建新的健康目标并关联运动类型",
            tags = {".06登录后健康目标计划操作"},
            responses = {
                    @ApiResponse(responseCode = "200",description = "创建健康目标成功"),
                    @ApiResponse(responseCode = "1014",description = "健康目标计划创建失败"),
                    @ApiResponse(responseCode = "1039",description = "添加新的【健康目标与运动类型之间的联系】的数据失败"),
            }
    )
    public Result<?> createHealthGoalInBatch(@RequestBody @Valid List<HealthGoalCreateReq> healthGoalCreateReqList) {
        healthGoalsService.createHealthGoalInBatch(healthGoalCreateReqList);
        return Result.success("创建健康目标成功");
    }

    @ApiOperationSupport(order = 2)
    @PutMapping
    @Operation(
            summary = "批量修改健康目标_前端已实现",
            description = "批量修改健康目标信息并关联运动类型",
            tags = {".06登录后健康目标计划操作"},
            responses = {
                    @ApiResponse(responseCode = "200",description = "修改健康目标成功"),
                    @ApiResponse(responseCode = "1015",description = "健康目标计划修改失败"),
                    @ApiResponse(responseCode = "1039",description = "添加新的【健康目标与运动类型之间的联系】的数据失败"),
                    @ApiResponse(responseCode = "1040",description = "删除现有的【健康目标与运动类型之间的联系】的数据失败"),
            }
    )
    public Result<?> updateHealthGoalInBatch(@RequestBody @Valid List<HealthGoalUpdateReq> healthGoalUpdateReqList) {
        healthGoalsService.updateHealthGoalInBatch(healthGoalUpdateReqList);
        return Result.success("修改健康目标成功");
    }

    @ApiOperationSupport(order = 3)
    @DeleteMapping
    @Operation(
            summary = "批量删除健康目标数据_前端已实现",
            description = "根据目标ID，批量删除健康目标数据",
            tags = {".06登录后健康目标计划操作"},
            responses = {
                    @ApiResponse(responseCode = "200",description = "删除健康目标成功"),
                    @ApiResponse(responseCode = "1041",description = "删除健康目标失败"),
            }
    )
    public Result<?> deleteHealthGoalInBatch(@RequestBody @Valid HealthGoalDeleteReq healthGoalDeleteReq) {
        boolean success = healthGoalsService.removeBatchByIds(healthGoalDeleteReq.getGoalIdList());
        if (!success) {
            throw new HealthGoalDeleteFailureException();
        }
        return Result.success("删除健康目标成功");
    }

    @ApiOperationSupport(order = 4)
    @PostMapping("/listByPage")
    @Operation(
            summary = "用户根据时间段来分页批量获取健康目标详情_前端已实现",
            description = "根据时间段来分页批量获取健康目标详情",
            tags = {".06登录后健康目标计划操作"},
            responses = {
                    @ApiResponse(responseCode = "200",description = "用户批量获取健康目标详情成功",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = HealthGoalsDetailsPageRespResult.class))),
                    @ApiResponse(responseCode = "200",description = "用户批量获取健康目标详情成功",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = EmptyPageRespResult.class))),
            }
    )
    public Result<?> listPaginatedHealthGoalsDetailsByTimeRange(@RequestBody @Valid HealthGoalsQueryReq healthGoalsQueryReq) {
        PageResponse<HealthGoalsDetailsDTO> pageResponse = healthGoalsService.listPaginatedHealthGoalsDetailsByTimeRange(healthGoalsQueryReq);
        if (pageResponse.getTotal() < 1){
            return Result.success(PageResponse.empty());
        }
        return Result.success(pageResponse);
    }
}