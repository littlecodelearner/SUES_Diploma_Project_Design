package sues.xmz.diploma.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import sues.xmz.diploma.domain.po.ExerciseTypes;

@Mapper
public interface ExerciseTypesMapper extends BaseMapper<ExerciseTypes> {
    // 带注解的mapper方法定义
    @Insert("insert into " +
            "diploma_project_daily_exercise_health_management_system.exercise_types (exercise_name) " +
            "values( #{exerciseTypesData.exerciseName})")
    int batchInsertExerciseTypes(@Param("exerciseTypesData") ExerciseTypes exerciseTypesData);

}