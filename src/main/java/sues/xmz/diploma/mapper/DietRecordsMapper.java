package sues.xmz.diploma.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import sues.xmz.diploma.domain.dto.diet_records.DietRecordsCalculationDTO;
import sues.xmz.diploma.domain.dto.diet_records.DietRecordsDTO;
import sues.xmz.diploma.domain.po.DietRecords;
import sues.xmz.diploma.domain.req.diet_records.DietRecordsCalculationReq;
import sues.xmz.diploma.domain.req.diet_records.DietRecordsQueryReq;

import java.util.List;

@Mapper
public interface DietRecordsMapper extends BaseMapper<DietRecords> {

    List<DietRecordsDTO> listPaginatedDietRecordsByTimeRange(
            @Param("query") DietRecordsQueryReq dietRecordsQueryReq,
            @Param("offset") long offset
    );

    List<DietRecordsDTO> listAllDietRecordsByTimeRange(@Param("query") DietRecordsQueryReq dietRecordsQueryReq);

    List<DietRecordsCalculationDTO> listDietRecordsDetailsByTimeRange(@Param("query") DietRecordsCalculationReq dietRecordsCalculationReq);
}