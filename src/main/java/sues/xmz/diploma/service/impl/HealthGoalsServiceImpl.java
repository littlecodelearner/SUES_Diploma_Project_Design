package sues.xmz.diploma.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sues.xmz.diploma.common.domain.PageResponse;
import sues.xmz.diploma.common.exception.health_goals.HealthGoalCreateFailureException;
import sues.xmz.diploma.common.exception.health_goals.HealthGoalUpdateFailureException;
import sues.xmz.diploma.common.exception.health_goals_exercise_types.HealthGoalsExerciseTypesSaveFailureException;
import sues.xmz.diploma.common.utils.BeansConvertUtil;
import sues.xmz.diploma.common.utils.PageUtil;
import sues.xmz.diploma.domain.dto.health_goals.HealthGoalsDetailsDTO;
import sues.xmz.diploma.domain.po.HealthGoals;
import sues.xmz.diploma.domain.po.HealthGoalsExerciseTypes;
import sues.xmz.diploma.domain.req.health_goals.HealthGoalCreateReq;
import sues.xmz.diploma.domain.req.health_goals.HealthGoalUpdateReq;
import sues.xmz.diploma.domain.req.health_goals.HealthGoalsQueryReq;
import sues.xmz.diploma.mapper.HealthGoalsMapper;
import sues.xmz.diploma.service.HealthGoalsExerciseTypesService;
import sues.xmz.diploma.service.HealthGoalsService;

import java.util.List;

@Service
@Transactional
public class HealthGoalsServiceImpl extends ServiceImpl<HealthGoalsMapper, HealthGoals> implements HealthGoalsService {

    @Resource
    private HealthGoalsExerciseTypesService healthGoalsExerciseTypesService;

    @Override
    public void createHealthGoalInBatch(List<HealthGoalCreateReq> healthGoalCreateReqList) {
        // 保存健康目标计划
        List<HealthGoals> healthGoalsList = BeanUtil.copyToList(healthGoalCreateReqList, HealthGoals.class);
        boolean success = this.saveBatch(healthGoalsList);
        if (!success) {
            throw new HealthGoalCreateFailureException();
        }

        // 保存健康目标计划与运动类型之间的关系
        List<HealthGoalsExerciseTypes> healthGoalsExerciseTypesList = BeansConvertUtil.copyToListOnlyHadBothId(
                healthGoalsList,
                healthGoalCreateReqList,
                HealthGoals::getGoalId,
                HealthGoalCreateReq::getExerciseTypeIdList,
                HealthGoalsExerciseTypes.class
        );
        if (healthGoalsExerciseTypesList.isEmpty()){
            return;
        }

        success = healthGoalsExerciseTypesService.saveBatch(healthGoalsExerciseTypesList);
        if (!success) {
            throw new HealthGoalsExerciseTypesSaveFailureException();
        }

    }

    @Override
    public void updateHealthGoalInBatch(List<HealthGoalUpdateReq> healthGoalUpdateReqList) {
        // 更新目标计划
        boolean success = this.updateBatchById(BeanUtil.copyToList(healthGoalUpdateReqList, HealthGoals.class));
        if (!success) {
            throw new HealthGoalUpdateFailureException();
        }

        // 更新关联的运动类型，返回是否
        List<HealthGoalsExerciseTypes> healthGoalsExerciseTypesList = BeansConvertUtil.copyToListOnlyHadBothId(
                healthGoalUpdateReqList,
                HealthGoalUpdateReq::getGoalId,
                HealthGoalUpdateReq::getExerciseTypeIdList,
                HealthGoalsExerciseTypes.class
        );
        if (healthGoalsExerciseTypesList.isEmpty()){
            return;
        }

        try {
            healthGoalsExerciseTypesService.updateHealthGoalsExerciseTypesInBatch(healthGoalsExerciseTypesList);
        } catch (Exception e) {
            throw new RuntimeException("健康目标计划更新失败" + e, e);
        }

    }

    /**
     * @Description: TODO：这里有个问题，因为我们的SQL语句是把所有数据进行分页，因此在联表查询时，
     *                我们做不到【只对某个表中数据的进行分页，同时又对另一个表中的数据不进行分页】这个情况。
     *                目前的解决方案就是：尽可能地把每页展示量加大（该项目默认是每页展示15个数据量）来避免以上情况。
     * @Author: Zachary Tsu
     * @Date: 2025/3/11 21:11
     */
    @Override
    public PageResponse<HealthGoalsDetailsDTO> listPaginatedHealthGoalsDetailsByTimeRange(HealthGoalsQueryReq healthGoalsQueryReq) {
        PageUtil.pageParamIfNull(healthGoalsQueryReq.getCurrent(),healthGoalsQueryReq.getSize());

        List<HealthGoalsDetailsDTO> healthGoalsDetailsDTOList = this.baseMapper.listPaginatedHealthGoalsDetailsByTimeRange(
                healthGoalsQueryReq,
                PageUtil.countOffset(healthGoalsQueryReq.getCurrent(), healthGoalsQueryReq.getSize())
        );

        return PageResponse.getPageResponse(
                healthGoalsDetailsDTOList,
                healthGoalsQueryReq.getCurrent(),
                healthGoalsQueryReq.getSize(),
        this.baseMapper.listAllHealthGoalsDetailsByTimeRange(healthGoalsQueryReq).size()
        );
    }
}
