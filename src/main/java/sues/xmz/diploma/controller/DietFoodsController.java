package sues.xmz.diploma.controller;


import cn.hutool.core.bean.BeanUtil;
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
import sues.xmz.diploma.common.exception.diet_foods.DietFoodsDeleteFailureException;
import sues.xmz.diploma.common.exception.diet_foods.DietFoodsSaveFailureException;
import sues.xmz.diploma.common.exception.diet_foods.DietFoodsUpdateFailureException;
import sues.xmz.diploma.domain.dto.diet_foods.DietFoodsDTO;
import sues.xmz.diploma.domain.po.DietFoods;
import sues.xmz.diploma.domain.req.diet_foods.DietFoodsAddReq;
import sues.xmz.diploma.domain.req.diet_foods.DietFoodsDeleteReq;
import sues.xmz.diploma.domain.req.diet_foods.DietFoodsQueryReq;
import sues.xmz.diploma.domain.req.diet_foods.DietFoodsUpdateReq;
import sues.xmz.diploma.domain.resp.diet_foods.ListAllDietFoodsDetailsResp;
import sues.xmz.diploma.domain.resp.diet_foods.ListPagedDietFoodsRespResult;
import sues.xmz.diploma.service.DietFoodsService;

import java.util.List;

/**
 * <p>
 * 食物表 前端控制器
 * </p>
 *
 * @author 徐铭泽
 * @since 2025-03-06
 */
@Tag(name = "04.1食物营养管理模块", description = "食物营养管理相关API")
@RestController
@RequestMapping("/diet-foods")
public class DietFoodsController {

    @Resource
    private DietFoodsService dietFoodsService;

    @ApiOperationSupport(order = 30)
    @Operation(
            tags = {"_管理员操作模块"},
            summary = "02食物数据操作-批量增加食物数据_前端已实现",
            description = "批量增加食物数据到食物表中",
            responses = {
                    @ApiResponse(responseCode = "200", description = "批量增加食物数据成功"),
                    @ApiResponse(responseCode = "1025", description = "批量增加食物数据失败")
            }
    )
    @PostMapping
    public Result<?> addDietFoodsInBatch(@RequestBody @Valid List<DietFoodsAddReq> dietFoodsAddReqList) {
        boolean success = dietFoodsService.saveBatch(BeanUtil.copyToList(dietFoodsAddReqList, DietFoods.class));
        if (!success) {
            throw new DietFoodsSaveFailureException();
        }
        return Result.success("批量增加食物数据成功");
    }

    @ApiOperationSupport(order = 31)
    @Operation(
            summary = "02食物数据操作-分页批量获取食物数据_前端已实现",
            tags = {"_管理员操作模块"},
            description = "分页批量获取食物数据【每条饮食记录的热量在前端计算和展示】",
            responses = {
                    @ApiResponse(responseCode = "200", description = "分页批量获取食物数据成功",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ListPagedDietFoodsRespResult.class))
                    ),
                    @ApiResponse(responseCode = "others", description = "分页批量获取食物数据失败",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = EmptyPageRespResult.class))
                    ),
            }
    )
    @PostMapping("/listByPage")
    public Result<?> listDietFoodsByPage(@RequestBody @Valid DietFoodsQueryReq dietFoodsQueryReq) {
        PageResponse<DietFoodsDTO> pageResponse = dietFoodsService.listDietFoodsByPage(dietFoodsQueryReq);

        if (pageResponse.getTotal() < 1) {
            return Result.success(PageResponse.empty());
        }
        return Result.success(pageResponse);
    }

    @ApiOperationSupport(order = 32)
    @Operation(
            summary = "02食物数据操作-批量修改食物数据_前端已实现",
            tags = {"_管理员操作模块"},
            description = "批量修改食物数据到食物表中",
            responses = {
                    @ApiResponse(responseCode = "200", description = "批量修改食物数据成功"),
                    @ApiResponse(responseCode = "1026", description = "批量修改食物数据失败")
            }
    )
    @PutMapping
    public Result<?> updateDietFoodsInBatch(@RequestBody @Valid List<DietFoodsUpdateReq> dietFoodsUpdateReqList) {
        boolean success = dietFoodsService.updateBatchById(BeanUtil.copyToList(dietFoodsUpdateReqList, DietFoods.class));
        if (!success) {
            throw new DietFoodsUpdateFailureException();
        }
        return Result.success("批量修改食物数据成功");
    }

    @ApiOperationSupport(order = 33)
    @Operation(
            summary = "02食物数据操作-批量删除食物数据_前端已实现",
            tags = {"_管理员操作模块"},
            description = "根据食物ID，来批量删除食物数据到食物表中",
            responses = {
                    @ApiResponse(responseCode = "200", description = "批量删除食物数据成功"),
                    @ApiResponse(responseCode = "1027", description = "批量删除食物数据失败")
            }
    )
    @DeleteMapping
    public Result<?> deleteDietFoodsInBatch(@RequestBody @Valid DietFoodsDeleteReq dietFoodsDeleteReq) {
        boolean success = dietFoodsService.removeBatchByIds(dietFoodsDeleteReq.getFoodIdList());
        if (!success) {
            throw new DietFoodsDeleteFailureException();
        }
        return Result.success("批量删除食物数据成功");
    }

    @ApiOperationSupport(order = 34)
    @Operation(
            summary = "02食物数据操作-获取所有食物数据_前端已实现",
            tags = {"_管理员操作模块"},
            description = "获取所有食物数据",
            responses = {
                    @ApiResponse(responseCode = "200", description = "获取所有食物数据成功",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ListAllDietFoodsDetailsResp.class))
                    ),
                    @ApiResponse(responseCode = "others", description = "没获取到所有食物数据"),
            }
    )
    @GetMapping
    public Result<?> listDietFoodsDetailsByPage() {
        return Result.success(dietFoodsService.list());
    }

    @ApiOperationSupport(order = 35)
    @Operation(
            summary = "02食物数据操作-获取所有的食物分类_前端已实现",
            tags = {"_管理员操作模块"},
            description = "获取所有的食物分类",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "获取所有的食物分类成功",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = Result.class,
                                            example = """
                                                    {
                                                      "code": 200,
                                                      "message": "成功",
                                                      "data": [
                                                        "水果",
                                                        "蔬菜",
                                                        "主食",
                                                        "肉类",
                                                        "...",
                                                        "其他"
                                                      ]
                                                    }
                                                    """
                                    )
                            )
                    ),
            }
    )
    @GetMapping("/foodTypes")
    public Result<?> getFoodTypes() {
        List<String> foodTypeList = dietFoodsService.lambdaQuery()
                .select(DietFoods::getFoodType)
                .list()
                .stream()
                .map(DietFoods::getFoodType)
                .distinct()
                .toList();

        if (foodTypeList.isEmpty()) {
            return Result.success("未找到相关食物分类");
        }
        return Result.success(foodTypeList);
    }


}
