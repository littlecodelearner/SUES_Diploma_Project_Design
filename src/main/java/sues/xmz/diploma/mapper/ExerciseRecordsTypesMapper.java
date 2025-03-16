package sues.xmz.diploma.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import sues.xmz.diploma.domain.po.ExerciseRecordsTypes;

import java.util.List;

@Mapper
public interface ExerciseRecordsTypesMapper extends BaseMapper<ExerciseRecordsTypes> {
    int updateExerciseRecordsInBatch(@Param("list") List<ExerciseRecordsTypes> exerciseRecordsTypesList);
}