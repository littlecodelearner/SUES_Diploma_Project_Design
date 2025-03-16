package sues.xmz.diploma.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import sues.xmz.diploma.domain.dto.health_goals.HealthGoalsDetailsDTO;
import sues.xmz.diploma.domain.po.HealthGoals;
import sues.xmz.diploma.domain.req.health_goals.HealthGoalsQueryReq;

import java.util.List;

@Mapper
public interface HealthGoalsMapper extends BaseMapper<HealthGoals> {

    List<HealthGoalsDetailsDTO> listPaginatedHealthGoalsDetailsByTimeRange(
            @Param("query") HealthGoalsQueryReq healthGoalsQueryReq,
            @Param("offset") long offset
    );

    List<HealthGoalsDetailsDTO> listAllHealthGoalsDetailsByTimeRange(
            @Param("query") HealthGoalsQueryReq healthGoalsQueryReq
    );
}