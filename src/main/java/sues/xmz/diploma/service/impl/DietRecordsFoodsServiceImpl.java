package sues.xmz.diploma.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sues.xmz.diploma.common.exception.diet_records_foods.DietRecordsFoodsDeleteFailureException;
import sues.xmz.diploma.common.exception.diet_records_foods.DietRecordsFoodsSaveFailureException;
import sues.xmz.diploma.domain.po.DietRecordsFoods;
import sues.xmz.diploma.mapper.DietRecordsFoodsMapper;
import sues.xmz.diploma.service.DietRecordsFoodsService;

import java.util.List;

/**
 * <p>
 * 饮食记录与食物的多对多关联表 服务实现类
 * </p>
 *
 * @author 徐铭泽
 * @since 2025-03-06
 */
@Service
@Transactional
public class DietRecordsFoodsServiceImpl extends ServiceImpl<DietRecordsFoodsMapper, DietRecordsFoods> implements DietRecordsFoodsService {

    /**
     * TODO：这里面不能用MyBatisPlus自带的方法来操作【双主键】的数据库表，会报错。
     *  <p>
     *  但是在{@link ExerciseRecordsTypesServiceImpl#updateExerciseRecordsInBatch(List)}中却可以，
     *  <p>
     *  明明这两个数据库表、Mapper层以及Service层的配置等情况都是一样的
     * @param dietRecordsFoodsList 实体类参数列表
     */
    @Override
    public void updateDietRecordsFoodsInBatch(List<DietRecordsFoods> dietRecordsFoodsList) {
        // 删除DietRecordsFoods表中的旧关系数据
        // TODO：这个Service层就不能用MyBatis自带的CRUD方法 - boolean success = this.removeBatchByIds(dietRecordsFoodsList.stream().map(DietRecordsFoods::getDietId).distinct().toList());
        boolean success = this.lambdaUpdate()
                .in(DietRecordsFoods::getDietId, dietRecordsFoodsList.stream().map(DietRecordsFoods::getDietId).distinct().toList())
                .remove();
        if (!success){
            throw new DietRecordsFoodsDeleteFailureException();
        }

        // 保存新关系数据到DietRecordsFoods表中
        // TODO：同理，不能用MyBatis自带的CRUD方法 - success = this.saveBatch(dietRecordsFoodsList);
        long insertCount = this.baseMapper.saveDietRecordsFoodsInBatch(dietRecordsFoodsList);
        if (insertCount != dietRecordsFoodsList.size()){
            throw new DietRecordsFoodsSaveFailureException();
        }

    }
}
