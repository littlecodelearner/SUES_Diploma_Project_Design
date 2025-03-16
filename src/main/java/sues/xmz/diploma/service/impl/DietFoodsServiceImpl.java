package sues.xmz.diploma.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import sues.xmz.diploma.common.domain.PageResponse;
import sues.xmz.diploma.common.utils.PageUtil;
import sues.xmz.diploma.domain.dto.diet_foods.DietFoodsDTO;
import sues.xmz.diploma.domain.po.DietFoods;
import sues.xmz.diploma.domain.req.diet_foods.DietFoodsQueryReq;
import sues.xmz.diploma.mapper.DietFoodsMapper;
import sues.xmz.diploma.service.DietFoodsService;

/**
 * <p>
 * 食物表 服务实现类
 * </p>
 *
 * @author 徐铭泽
 * @since 2025-03-06
 */
@Service
public class DietFoodsServiceImpl extends ServiceImpl<DietFoodsMapper, DietFoods> implements DietFoodsService {

    @Override
    public PageResponse<DietFoodsDTO> listDietFoodsByPage(DietFoodsQueryReq dietFoodsQueryReq) {
        PageUtil.pageParamIfNull(dietFoodsQueryReq.getCurrent(), dietFoodsQueryReq.getSize());

        Page<DietFoods> pageResult = this.lambdaQuery()
                .like(dietFoodsQueryReq.getFoodName() != null && !dietFoodsQueryReq.getFoodName().isBlank(),
                        DietFoods::getFoodName,
                        dietFoodsQueryReq.getFoodName())
                .like(dietFoodsQueryReq.getFoodType() != null && !dietFoodsQueryReq.getFoodType().isBlank(),
                        DietFoods::getFoodType,
                        dietFoodsQueryReq.getFoodType())
                .page(Page.of(dietFoodsQueryReq.getCurrent(), dietFoodsQueryReq.getSize()));

        return PageResponse.getPageResponse(pageResult, DietFoodsDTO.class);
    }
}
