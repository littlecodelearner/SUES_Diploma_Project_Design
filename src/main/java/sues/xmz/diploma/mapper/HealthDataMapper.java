package sues.xmz.diploma.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import sues.xmz.diploma.domain.dto.health_data.HealthDataDTO;
import sues.xmz.diploma.domain.po.HealthData;
import sues.xmz.diploma.domain.req.health_data.HealthDataQueryReq;

import java.util.List;

@Mapper
public interface HealthDataMapper extends BaseMapper<HealthData> {
    List<HealthDataDTO> listPaginatedHealthDataByTimeRange(
            @Param("query") HealthDataQueryReq healthDataQueryReq,
            @Param("offset") long offset
    );

    List<HealthDataDTO> listAllHealthDataByTimeRange(@Param("query") HealthDataQueryReq healthDataQueryReq);

}