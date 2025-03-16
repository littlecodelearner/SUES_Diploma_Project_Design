package sues.xmz.diploma.service;

import com.baomidou.mybatisplus.extension.service.IService;
import sues.xmz.diploma.common.domain.PageResponse;
import sues.xmz.diploma.domain.dto.exercise_records_types.ExerciseRecordsDTO;
import sues.xmz.diploma.domain.po.ExerciseRecords;
import sues.xmz.diploma.domain.req.exercise_records.ExerciseRecordUpdateReq;
import sues.xmz.diploma.domain.req.exercise_records.ExerciseRecordsCreateReq;
import sues.xmz.diploma.domain.req.exercise_records.ExerciseRecordsQueryReq;

import java.util.List;

public interface ExerciseRecordsService extends IService<ExerciseRecords>{
    void saveExerciseRecordInBatch(List<ExerciseRecordsCreateReq> exerciseRecordsCreateReqList);

    void updateExerciseRecordInBatch(List<ExerciseRecordUpdateReq> exerciseRecordUpdateReqList);

    PageResponse<ExerciseRecordsDTO> listPaginatedExerciseRecordsByTimeRange(ExerciseRecordsQueryReq exerciseRecordsQueryReq);
}
