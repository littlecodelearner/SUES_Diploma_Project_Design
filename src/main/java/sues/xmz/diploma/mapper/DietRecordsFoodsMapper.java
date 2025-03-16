package sues.xmz.diploma.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import sues.xmz.diploma.domain.po.DietRecordsFoods;

import java.util.List;

/**
 * <p>
 * 饮食记录与食物的多对多关联表 Mapper 接口
 * </p>
 *
 * @author 徐铭泽
 * @since 2025-03-06
 */
@Mapper
public interface DietRecordsFoodsMapper extends BaseMapper<DietRecordsFoods> {

    long saveDietRecordsFoodsInBatch(@Param("list") List<DietRecordsFoods> dietRecordsFoodsList);
}
