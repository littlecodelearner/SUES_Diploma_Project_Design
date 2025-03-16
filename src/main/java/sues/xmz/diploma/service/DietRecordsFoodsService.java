package sues.xmz.diploma.service;

import com.baomidou.mybatisplus.extension.service.IService;
import sues.xmz.diploma.domain.po.DietRecordsFoods;

import java.util.List;

/**
 * <p>
 * 饮食记录与食物的多对多关联表 服务类
 * </p>
 *
 * @author 徐铭泽
 * @since 2025-03-06
 */
public interface DietRecordsFoodsService extends IService<DietRecordsFoods> {

    void updateDietRecordsFoodsInBatch(List<DietRecordsFoods> dietRecordsFoodsList);
}
