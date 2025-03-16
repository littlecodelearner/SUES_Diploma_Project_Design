package sues.xmz.diploma.service;

import com.baomidou.mybatisplus.extension.service.IService;
import sues.xmz.diploma.common.domain.PageResponse;
import sues.xmz.diploma.domain.dto.health_goals.HealthGoalsDetailsDTO;
import sues.xmz.diploma.domain.po.HealthGoals;
import sues.xmz.diploma.domain.req.health_goals.HealthGoalCreateReq;
import sues.xmz.diploma.domain.req.health_goals.HealthGoalUpdateReq;
import sues.xmz.diploma.domain.req.health_goals.HealthGoalsQueryReq;

import java.util.List;

public interface HealthGoalsService extends IService<HealthGoals>{
    void createHealthGoalInBatch(List<HealthGoalCreateReq> healthGoalCreateReqList);

    void updateHealthGoalInBatch(List<HealthGoalUpdateReq> healthGoalUpdateReqList);

    PageResponse<HealthGoalsDetailsDTO> listPaginatedHealthGoalsDetailsByTimeRange(HealthGoalsQueryReq healthGoalsQueryReq);

}
