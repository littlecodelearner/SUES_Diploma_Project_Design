package sues.xmz.diploma.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import sues.xmz.diploma.domain.po.HealthGoalsExerciseTypes;

import java.util.List;

@Mapper
public interface HealthGoalsExerciseTypesMapper extends BaseMapper<HealthGoalsExerciseTypes> {

    List<HealthGoalsExerciseTypes> selectHealthGoalsExerciseTypesInBatch(@Param("idList") List<Integer> idList);

    long deleteHealthGoalsExerciseTypesInBatch(@Param("idList") List<Integer> goalIdlist);

    long insertHealthGoalsExerciseTypesInBatch(@Param("list") List<HealthGoalsExerciseTypes> healthGoalsExerciseTypesList);

}