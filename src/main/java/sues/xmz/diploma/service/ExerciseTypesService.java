package sues.xmz.diploma.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.executor.BatchResult;
import sues.xmz.diploma.common.domain.PageResponse;
import sues.xmz.diploma.domain.po.ExerciseTypes;
import sues.xmz.diploma.domain.req.exercise_types.ExerciseTypesCreateReq;
import sues.xmz.diploma.domain.req.exercise_types.ExerciseTypesUpdateReq;

import java.util.List;

public interface ExerciseTypesService extends IService<ExerciseTypes>{

    List<BatchResult> addExerciseTypesInBatch(ExerciseTypesCreateReq exerciseTypesCreateReq);

    List<BatchResult> updateExerciseTypesInBatch(List<ExerciseTypesUpdateReq> exerciseTypesUpdateReqList);

    PageResponse<ExerciseTypes> listExerciseTypesByPage(Long current, Long size, String name);
}
