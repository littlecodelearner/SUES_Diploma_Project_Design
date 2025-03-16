package sues.xmz.diploma.service;

import com.baomidou.mybatisplus.extension.service.IService;
import sues.xmz.diploma.common.domain.PageResponse;
import sues.xmz.diploma.domain.dto.diet_foods.DietFoodsDTO;
import sues.xmz.diploma.domain.po.DietFoods;
import sues.xmz.diploma.domain.req.diet_foods.DietFoodsQueryReq;

/**
 * <p>
 * 食物表 服务类
 * </p>
 *
 * @author 徐铭泽
 * @since 2025-03-06
 */
public interface DietFoodsService extends IService<DietFoods> {

    PageResponse<DietFoodsDTO> listDietFoodsByPage(DietFoodsQueryReq dietFoodsQueryReq);
}
