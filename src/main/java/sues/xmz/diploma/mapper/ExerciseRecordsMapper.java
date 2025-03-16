package sues.xmz.diploma.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import sues.xmz.diploma.domain.dto.exercise_records_types.ExerciseRecordsDTO;
import sues.xmz.diploma.domain.po.ExerciseRecords;
import sues.xmz.diploma.domain.req.exercise_records.ExerciseRecordsQueryReq;

import java.util.List;

@Mapper
public interface ExerciseRecordsMapper extends BaseMapper<ExerciseRecords> {

    List<ExerciseRecordsDTO> listPaginatedExerciseRecordsDetailsByTimeRange(
            @Param("query") ExerciseRecordsQueryReq exerciseRecordsQueryReq,
            @Param("offset") long offset);

    List<ExerciseRecordsDTO> listAllExerciseRecordsByTimeRange(@Param("query") ExerciseRecordsQueryReq exerciseRecordsQueryReq);
}