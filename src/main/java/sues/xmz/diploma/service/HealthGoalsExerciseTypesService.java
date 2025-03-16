package sues.xmz.diploma.service;

import com.baomidou.mybatisplus.extension.service.IService;
import sues.xmz.diploma.domain.po.HealthGoalsExerciseTypes;

import java.util.List;

public interface HealthGoalsExerciseTypesService extends IService<HealthGoalsExerciseTypes>{

    void updateHealthGoalsExerciseTypesInBatch(List<HealthGoalsExerciseTypes> healthGoalsExerciseTypesList);
}
