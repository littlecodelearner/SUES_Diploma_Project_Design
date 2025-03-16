package sues.xmz.diploma.service;

import com.baomidou.mybatisplus.extension.service.IService;
import sues.xmz.diploma.domain.po.ExerciseRecordsTypes;

import java.util.List;

public interface ExerciseRecordsTypesService extends IService<ExerciseRecordsTypes>{

    void updateExerciseRecordsInBatch(List<ExerciseRecordsTypes> exerciseRecordsTypesList);
}
