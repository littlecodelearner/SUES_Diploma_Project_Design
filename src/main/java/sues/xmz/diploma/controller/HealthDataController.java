package sues.xmz.diploma.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;
import sues.xmz.diploma.common.domain.EmptyPageRespResult;
import sues.xmz.diploma.common.domain.PageResponse;
import sues.xmz.diploma.common.domain.Result;
import sues.xmz.diploma.common.exception.health_data.HealthDataCreateFailure;
import sues.xmz.diploma.common.exception.health_data.HealthDataDeleteFailure;
import sues.xmz.diploma.common.exception.health_data.HealthDataUpdateFailure;
import sues.xmz.diploma.common.utils.ImageUtil;
import sues.xmz.diploma.domain.dto.health_data.HealthDataDTO;
import sues.xmz.diploma.domain.po.HealthData;
import sues.xmz.diploma.domain.req.health_data.HealthDataCreateReq;
import sues.xmz.diploma.domain.req.health_data.HealthDataDeleteReq;
import sues.xmz.diploma.domain.req.health_data.HealthDataQueryReq;
import sues.xmz.diploma.domain.req.health_data.HealthDataUpdateReq;
import sues.xmz.diploma.domain.resp.health_data.HealDataDetailsPageRespResult;
import sues.xmz.diploma.domain.resp.health_data.HealDataDetailsResp;
import sues.xmz.diploma.service.HealthDataService;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Tag(name = "03健康数据管理", description = "健康数据相关API")
@RequestMapping("/healthData")
@RestController
public class HealthDataController {

    @Resource
    private HealthDataService healthDataService;

    @ApiOperationSupport(order = 1)
    @Operation(
            summary = "批量添加健康数据_前端已实现",
            description = "用户批量添加新的健康数据记录",
            tags = {".03登录后健康数据操作"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "添加新的健康数据记录成功"),
                    @ApiResponse(responseCode = "1036", description = "添加新的健康数据记录失败")
            }
    )
    @PostMapping
    public Result<?> addHealthDataInBatch(@RequestBody @Valid List<HealthDataCreateReq> healthDataCreateReqList) {
        boolean success = healthDataService.addHealthDataInBatch(healthDataCreateReqList);
        if (!success) {
            throw new HealthDataCreateFailure();
        }
        return Result.success("添加新的健康数据记录成功");
    }

    @ApiOperationSupport(order = 2)
    @Operation(
            summary = "批量修改健康数据_前端已实现",
            description = "用户批量修改现有的健康数据记录",
            tags = {".03登录后健康数据操作"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "修改现有的健康数据记录成功"),
                    @ApiResponse(responseCode = "1008", description = "健康数据没找到"),
                    @ApiResponse(responseCode = "1037", description = "修改现有的健康数据记录失败"),
            }
    )
    @PutMapping
    public Result<?> updateHealthDataInBatch(@RequestBody @Valid List<HealthDataUpdateReq> healthDataUpdateReqList) {
        boolean success = healthDataService.updateHealthDataInBatch(healthDataUpdateReqList);
        if (!success) {
            throw new HealthDataUpdateFailure();
        }
        return Result.success("修改现有的健康数据记录成功");
    }

    @ApiOperationSupport(order = 3)
    @Operation(
            summary = "用户批量删除健康数据_前端已实现",
            description = "根据健康数据ID，批量删除指定的健康数据记录",
            tags = {".03登录后健康数据操作"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "删除指定的健康数据记录成功"),
                    @ApiResponse(responseCode = "1038", description = "删除指定的健康数据记录失败"),
            }
    )
    @DeleteMapping
    public Result<?> deleteHealthDataInBulk(@RequestBody @Valid HealthDataDeleteReq healthDataDeleteReq) {
        boolean success = healthDataService.deleteHealthData(healthDataDeleteReq.getHealthDataIdList());
        if (!success) {
            throw new HealthDataDeleteFailure();
        }
        return Result.success("删除指定的健康数据记录成功");
    }

    @ApiOperationSupport(order = 4)
    @Operation(
            summary = "用户根据时间段来分页批量查看所有健康数据_前端已实现",
            description = "根据用户ID和日期范围等参数分页查询健康数据",
            tags = {".03登录后健康数据操作"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "获取健康数据成功",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = HealDataDetailsPageRespResult.class))
                    ),
                    @ApiResponse(responseCode = "others", description = "获取健康数据失败",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = EmptyPageRespResult.class))
                    ),
            }
    )
    @PostMapping("/listByPage")
    public Result<?> listPaginatedHealthDataByTimeRange(@RequestBody @Valid HealthDataQueryReq healthDataQueryReq) {
        PageResponse<HealthDataDTO> pageResponse = healthDataService.listPaginatedHealthDataByTimeRange(healthDataQueryReq);
        if (pageResponse.getTotal() < 1) {
            return Result.success(PageResponse.empty());
        }
        return Result.success(pageResponse);
    }

    @ApiOperationSupport(order = 5)
    @Operation(
            summary = "用户查看所有健康数据_前端已实现",
            description = "用户查看所有健康数据",
            tags = {".03登录后健康数据操作"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "获取所有健康数据成功",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = HealDataDetailsResp.class))
                    ),
                    @ApiResponse(responseCode = "others", description = "获取所有健康数据失败"),
            }
    )
    @GetMapping
    public Result<?> getAllHealthData(@RequestParam @NotNull(message = "用户ID不能为空")
                                      @Schema(description = "用户ID") Integer userId) {
        return Result.success(healthDataService
                .list(Wrappers.<HealthData>lambdaQuery()
                        .eq(HealthData::getUserId, userId))
        );
    }

    /**
     * <pre>
     * 前端可以根据 Base64字符串imageBytesBase64String 生成图片，例如：
     *     {@code
     *      const imageSrc = 'data:image/png;base64,' + imageBytesBase64String;
     *      const imgElement = document.createElement('img');
     *      imgElement.src = imageSrc;
     *      document.body.appendChild(imgElement);
     *     }
     * </pre>
     *
     * @param healthDataQueryReq 查询健康数据的参数
     *
     * @return imageBytesBase64String
     */
    @ApiOperationSupport(order = 100)
    @PostMapping("/trendChart")
    @Operation(
            summary = "用户查看健康数据趋势图表_前端暂时不实现",
            tags = {".03登录后健康数据操作"},
            description =
                    """
                            获取健康数据的趋势图表
                                 <pre>
                                 前端可以根据 Base64字符串imageBytesBase64String 生成图片，例如：
                                      {@code
                                       const imageSrc = 'data:image/png;base64,' + imageBytesBase64String;
                                       const imgElement = document.createElement('img');
                                       imgElement.src = imageSrc;
                                       document.body.appendChild(imgElement);
                                      }
                                 </pre>
                            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "获取健康数据的趋势图表成功"),
                    @ApiResponse(responseCode = "1008", description = "健康数据没找到"),
            }
    )
    public Result<?> getHealthDataTrend(@RequestBody @Valid HealthDataQueryReq healthDataQueryReq) {

        try {
            // 生成图表的字节数组
            byte[] imageBytes = healthDataService.getHealthDataTrend(healthDataQueryReq);
            ImageUtil.saveImage(imageBytes, "check_image/trend.png");

            // 将图表图片编码为Base64字符串
            String imageBytesBase64String = Base64.getEncoder().encodeToString(imageBytes);

            // 返回成功结果
            return Result.success(Map.of("ImageOfBase64String", imageBytesBase64String));
        } catch (IOException e) {
            throw new RuntimeException("健康数据趋势图生成错误：" + e.getLocalizedMessage(), e);
        }
    }
}