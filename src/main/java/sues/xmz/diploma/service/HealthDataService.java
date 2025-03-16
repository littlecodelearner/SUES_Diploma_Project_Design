package sues.xmz.diploma.service;

import com.baomidou.mybatisplus.extension.service.IService;
import sues.xmz.diploma.common.domain.PageResponse;
import sues.xmz.diploma.domain.dto.health_data.HealthDataDTO;
import sues.xmz.diploma.domain.po.HealthData;
import sues.xmz.diploma.domain.req.health_data.HealthDataCreateReq;
import sues.xmz.diploma.domain.req.health_data.HealthDataQueryReq;
import sues.xmz.diploma.domain.req.health_data.HealthDataUpdateReq;

import java.io.IOException;
import java.util.List;

public interface HealthDataService extends IService<HealthData>{

    boolean addHealthDataInBatch(List<HealthDataCreateReq> healthDataCreateReqList);

    boolean updateHealthDataInBatch(List<HealthDataUpdateReq> healthDataUpdateReqList);

    boolean deleteHealthData(List<Integer> healthDataIdList);

    PageResponse<HealthDataDTO> listPaginatedHealthDataByTimeRange(HealthDataQueryReq healthDataQueryReq);

    byte[] getHealthDataTrend(HealthDataQueryReq healthDataQueryReq) throws IOException;

}
