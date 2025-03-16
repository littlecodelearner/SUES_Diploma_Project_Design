package sues.xmz.diploma.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import sues.xmz.diploma.common.domain.Result;
import sues.xmz.diploma.domain.dto.health_profiles.HealthProfileDTO;
import sues.xmz.diploma.domain.req.health_profiles.HealthProfileUpdateReq;
import sues.xmz.diploma.domain.req.health_profiles.HealthProfilesCreateReq;
import sues.xmz.diploma.domain.resp.health_profiles.HealthProfileDetialRespResult;
import sues.xmz.diploma.service.HealthProfilesService;

@Tag(name = "02健康档案管理", description = "健康档案相关API")
@RequestMapping("/healthProfile")
@RestController
public class HealthProfilesController {

    @Resource
    private HealthProfilesService healthProfilesService;

    @ApiOperationSupport(order = 1)
    @PostMapping("/create")
    @Operation(
            tags = {".01登录后的健康档案界面"},
            summary = "创建健康档案_前端已经实现",
            description = "用户创建自己的健康档案",
            responses = {
                    @ApiResponse(responseCode = "200", description = "创建健康档案成功"),
                    @ApiResponse(responseCode = "1006", description = "每个用户只能创建且拥有一个健康档案"),
                    @ApiResponse(responseCode = "1042", description = "创建健康档案失败"),
            }
    )
    public Result<?> createHealthProfile(@RequestBody @Valid HealthProfilesCreateReq healthProfilesCreateReq) {
        healthProfilesService.createHealthProfile(healthProfilesCreateReq);
        return Result.success("创建健康档案成功");
    }

    @ApiOperationSupport(order = 2)
    @GetMapping("/get/{userId}")
    @Operation(
            tags = {".01登录后的健康档案界面"},
            summary = "获取健康档案_前端已经实现",
            description = "根据用户ID获取健康档案（每个用户只有一份健康档案）",
            responses = {
                    @ApiResponse(responseCode = "200", description = "用户首页（用户健康档案）获取成功",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = HealthProfileDetialRespResult.class))
                    ),
                    @ApiResponse(responseCode = "1005", description = "用户首页（用户健康档案）信息获取失败"),
            }
    )
    public Result<HealthProfileDTO> getHealthProfile(@PathVariable @Parameter(description = "用户ID")
                                                     Integer userId) {
        return Result.success(healthProfilesService.getHealthProfileByUserId(userId));
    }


    @ApiOperationSupport(order = 3)
    @PutMapping("/update")
    @Operation(
            tags = {".01登录后的健康档案界面"},
            summary = "更新健康档案_前端已经实现",
            description = "用户更新自己的健康档案",
            responses = {
                    @ApiResponse(responseCode = "200", description = "更新健康档案成功"),
                    @ApiResponse(responseCode = "1005", description = "健康档案没有找到"),
                    @ApiResponse(responseCode = "1043", description = "更新健康档案失败"),
            }
    )
    public Result<?> updateHealthProfile(@RequestBody @Valid HealthProfileUpdateReq healthProfileUpdateReq) {
        healthProfilesService.updateHealthProfile(healthProfileUpdateReq);
        return Result.success("更新健康档案成功");
    }

    @ApiOperationSupport(order = 4)
    @Operation(
            tags = {".01登录后的健康档案界面"},
            summary = "根据用户ID删除健康档案_前端已经实现",
            description = "根据用户ID删除健康档案",
            responses = {
                    @ApiResponse(responseCode = "200", description = "删除健康档案成功"),
                    @ApiResponse(responseCode = "1005", description = "健康档案没有找到"),
                    @ApiResponse(responseCode = "1044", description = "删除健康档案失败"),
            }
    )
    @DeleteMapping("/delete/{userId}")
    public Result<?> deleteHealthProfile(@PathVariable @Parameter(description = "用户ID")
                                             Integer userId) {
        healthProfilesService.deleteHealthProfileByUserId(userId);
        return Result.success("删除健康档案成功");
    }
}
