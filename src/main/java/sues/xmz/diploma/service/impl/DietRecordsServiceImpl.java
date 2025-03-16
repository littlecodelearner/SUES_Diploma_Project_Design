package sues.xmz.diploma.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sues.xmz.diploma.common.domain.PageResponse;
import sues.xmz.diploma.common.domain.ResultCode;
import sues.xmz.diploma.common.exception.diet_records.DietRecordsSaveFailureException;
import sues.xmz.diploma.common.exception.diet_records.DietRecordsUpdateFailureException;
import sues.xmz.diploma.common.exception.diet_records_foods.DietRecordsFoodsSaveFailureException;
import sues.xmz.diploma.common.utils.PageUtil;
import sues.xmz.diploma.domain.dto.diet_records.DietRecordsCalculationDTO;
import sues.xmz.diploma.domain.dto.diet_records.DietRecordsDTO;
import sues.xmz.diploma.domain.dto.diet_records.FoodsQuantitiesDTO;
import sues.xmz.diploma.domain.po.DietRecords;
import sues.xmz.diploma.domain.po.DietRecordsFoods;
import sues.xmz.diploma.domain.req.diet_records.DietRecordsCalculationReq;
import sues.xmz.diploma.domain.req.diet_records.DietRecordsCreateReq;
import sues.xmz.diploma.domain.req.diet_records.DietRecordsQueryReq;
import sues.xmz.diploma.domain.req.diet_records.DietRecordsUpdateReq;
import sues.xmz.diploma.domain.resp.diet_records.NutritionalCalculationConsequenceResp;
import sues.xmz.diploma.mapper.DietRecordsMapper;
import sues.xmz.diploma.service.DietRecordsFoodsService;
import sues.xmz.diploma.service.DietRecordsService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class DietRecordsServiceImpl extends ServiceImpl<DietRecordsMapper, DietRecords> implements DietRecordsService {

    private final DietRecordsFoodsService dietRecordsFoodsService;

    @Override
    public void addDietRecordsInBulk(List<DietRecordsCreateReq> dietRecordsCreateReqList) {
        // 转换为实体类
        List<DietRecords> dietRecordList = BeanUtil.copyToList(dietRecordsCreateReqList, DietRecords.class);
        // 保存DietRecords数据
        boolean success = this.saveBatch(dietRecordList);
        if (!success) {
            throw new DietRecordsSaveFailureException();
        }

        // TODO：保存所有DietRecords数据时，dietRecordList对象会自动生成dietId，所以后续可以直接使用dietRecordList来获取所有的dietId
        // 保存DietRecordsFoods数据
        List<DietRecordsFoods> dietRecordsFoodsList = IntStream.range(0, dietRecordList.size())
                .boxed()
                .flatMap(index -> {
                    DietRecords dietRecord = dietRecordList.get(index);
                    List<FoodsQuantitiesDTO> foodsQuantitiesDTOList = dietRecordsCreateReqList.get(index).getFoodsQuantitiesDTOList();
                    return foodsQuantitiesDTOList.stream()
                            .map(foodsQuantitiesDTO -> new DietRecordsFoods(
                                            dietRecord.getDietId(),
                                            foodsQuantitiesDTO.getFoodId(),
                                            foodsQuantitiesDTO.getQuantity()
                                    )
                            );
                })
                .toList();
        success = dietRecordsFoodsService.saveBatch(dietRecordsFoodsList);
        if (!success) {
            throw new DietRecordsFoodsSaveFailureException();
        }
    }

    @Override
    public void updateDietRecordsInBulk(List<DietRecordsUpdateReq> dietRecordsUpdateReqList) {
        // 修改DietRecords表数据
        boolean success = this.updateBatchById(BeanUtil.copyToList(dietRecordsUpdateReqList, DietRecords.class));
        if (!success) {
            throw new DietRecordsUpdateFailureException();
        }

        // 更新DietRecordsFoods表中的关系数据
        List<DietRecordsFoods> dietRecordsFoodsList = IntStream.range(0, dietRecordsUpdateReqList.size())
                .boxed()
                .flatMap(index -> {
                    Integer dietId = dietRecordsUpdateReqList.get(index).getDietId();
                    List<FoodsQuantitiesDTO> foodsQuantitiesDTOList = dietRecordsUpdateReqList.get(index).getFoodsQuantitiesDTOList();
                    return foodsQuantitiesDTOList.stream()
                            .map(foodsQuantitiesDTO -> new DietRecordsFoods(
                                            dietId,
                                            foodsQuantitiesDTO.getFoodId(),
                                            foodsQuantitiesDTO.getQuantity()
                                    )
                            );
                })
                .toList();
        try {
            dietRecordsFoodsService.updateDietRecordsFoodsInBatch(dietRecordsFoodsList);
        } catch (Exception e) {
            throw new RuntimeException(ResultCode.DIET_RECORDS_FOODS_UPDATE_FAILURE.getMessage()+e.getLocalizedMessage(),e);
        }

    }

    @Override
    public PageResponse<DietRecordsDTO> listPaginatedDietRecordsByTimeRange(DietRecordsQueryReq dietRecordsQueryReq) {
        // 参数验证
        PageUtil.pageParamIfNull(dietRecordsQueryReq.getCurrent(), dietRecordsQueryReq.getSize());

        // TODO: 练习使用Mybatis的XML文件，通过SQL语句进行分页批量查询
        List<DietRecordsDTO> paginatedDietRecordsDTOList = this.baseMapper.listPaginatedDietRecordsByTimeRange(
                dietRecordsQueryReq,
                PageUtil.countOffset(dietRecordsQueryReq.getCurrent(), dietRecordsQueryReq.getSize())
        );

        return PageResponse.getPageResponse(
                paginatedDietRecordsDTOList,
                dietRecordsQueryReq.getCurrent(),
                dietRecordsQueryReq.getSize(),
                this.baseMapper.listAllDietRecordsByTimeRange(dietRecordsQueryReq).size()
        );
    }

    @Override
    public NutritionalCalculationConsequenceResp calculateNutritionIntake(DietRecordsCalculationReq dietRecordsCalculationReq) {
        // 获取数据
        List<DietRecordsCalculationDTO> dietRecordsCalculationDTOList = this.baseMapper.listDietRecordsDetailsByTimeRange(dietRecordsCalculationReq);
        // 如果列表为空或null，返回默认结果
        if (dietRecordsCalculationDTOList == null || dietRecordsCalculationDTOList.isEmpty()) {
            return NutritionalCalculationConsequenceResp.builder()
                    .totalCalories(BigDecimal.ZERO)
                    .totalProtein(BigDecimal.ZERO)
                    .totalFat(BigDecimal.ZERO)
                    .totalCarbohydrates(BigDecimal.ZERO)
                    .totalWater(BigDecimal.ZERO)
                    .dietRecordsList(dietRecordsCalculationDTOList)
                    .build();
        }

        // 开始计算
        // 计算总卡路里
        BigDecimal totalCalories = calculateTotal(dietRecordsCalculationDTOList, DietRecordsCalculationDTO::getCalories);
        // 计算总蛋白质
        BigDecimal totalProtein = calculateTotal(dietRecordsCalculationDTOList, dto -> getOrZero(dto.getProtein()));
        // 计算总脂肪
        BigDecimal totalFat = calculateTotal(dietRecordsCalculationDTOList, dto -> getOrZero(dto.getFat()));
        // 计算总碳水化合物
        BigDecimal totalCarbohydrates = calculateTotal(dietRecordsCalculationDTOList, dto -> getOrZero(dto.getCarbohydrates()));
        // 计算总水分
        BigDecimal totalWater = calculateTotal(dietRecordsCalculationDTOList, dto -> getOrZero(dto.getWater()));

        // 返回计算结果
        return NutritionalCalculationConsequenceResp.builder()
                .totalCalories(totalCalories)
                .totalProtein(totalProtein)
                .totalFat(totalFat)
                .totalCarbohydrates(totalCarbohydrates)
                .totalWater(totalWater)
                .dietRecordsList(dietRecordsCalculationDTOList)
                .build();
    }


    /**
     * 处理可能为null的营养值，若为null则返回BigDecimal.ZERO
     *
     * @param value 营养值
     *
     * @return 非null的BigDecimal值
     */
    private static BigDecimal getOrZero(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    /**
     * 原始代码参考
     * <pre>
     * BigDecimal totalProtein = dietRecordsDTOList.stream()
     *                  .map(dto -> getOrZero(dto.getProtein())
     *                      .multiply(dto.getQuantity())
     *                      .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
     *                   )
     *                  .reduce(BigDecimal.ZERO, BigDecimal::add);
     * </pre>
     *
     * @param dietRecordsDTOList 饮食记录数据列表
     * @param nutrientExtractor  获取某种营养的值
     *
     * @return 计算后得到的总真实值
     */
    private BigDecimal calculateTotal(List<DietRecordsCalculationDTO> dietRecordsDTOList, Function<DietRecordsCalculationDTO, BigDecimal> nutrientExtractor) {
        return dietRecordsDTOList.stream()
                .map(dto -> {
                    BigDecimal nutrient = nutrientExtractor.apply(dto);
                    BigDecimal quantity = dto.getQuantity();
                    if (nutrient != null && quantity != null) {
                        return nutrient.multiply(quantity).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                    } else {
                        return BigDecimal.ZERO;
                    }
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


}
