package sues.xmz.diploma.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sues.xmz.diploma.common.exception.health_goals_exercise_types.HealthGoalsExerciseTypesDeleteFailureException;
import sues.xmz.diploma.common.exception.health_goals_exercise_types.HealthGoalsExerciseTypesSaveFailureException;
import sues.xmz.diploma.domain.po.HealthGoalsExerciseTypes;
import sues.xmz.diploma.mapper.HealthGoalsExerciseTypesMapper;
import sues.xmz.diploma.service.HealthGoalsExerciseTypesService;

import java.util.List;

/**
 * 遇到了与{@link sues.xmz.diploma.service.impl.DietRecordsFoodsServiceImpl}一样的问题
 * @Author: Zachary Tsu
 * @Date: 2025/3/11 19:27
 */
@Service
@Transactional
public class HealthGoalsExerciseTypesServiceImpl extends ServiceImpl<HealthGoalsExerciseTypesMapper, HealthGoalsExerciseTypes> implements HealthGoalsExerciseTypesService {

    @Override
    public void updateHealthGoalsExerciseTypesInBatch(List<HealthGoalsExerciseTypes> healthGoalsExerciseTypesList) {
        List<Integer> goalIdlist = healthGoalsExerciseTypesList.stream().map(HealthGoalsExerciseTypes::getGoalId).toList();

        // 查看是否存在原来的数据
        List<HealthGoalsExerciseTypes> selectedDataList = this.baseMapper.selectHealthGoalsExerciseTypesInBatch(goalIdlist);
        if (selectedDataList.isEmpty()) {
            return;
        }

        // 删除原来的关系数据
        try {
            this.baseMapper.deleteHealthGoalsExerciseTypesInBatch(goalIdlist);
        } catch (Exception e) {
            throw new HealthGoalsExerciseTypesDeleteFailureException();
        }

        // 添加新的关系数据
        long insertCount = this.baseMapper.insertHealthGoalsExerciseTypesInBatch(healthGoalsExerciseTypesList);
        if (insertCount != healthGoalsExerciseTypesList.size()){
            throw new HealthGoalsExerciseTypesSaveFailureException();
        }

    }

}
