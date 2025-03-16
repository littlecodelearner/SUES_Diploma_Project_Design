package sues.xmz.diploma.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sues.xmz.diploma.common.exception.exercise_records_types.ExerciseRecordsTypesSaveFailException;
import sues.xmz.diploma.domain.po.ExerciseRecordsTypes;
import sues.xmz.diploma.mapper.ExerciseRecordsTypesMapper;
import sues.xmz.diploma.service.ExerciseRecordsTypesService;

import java.util.List;

@Service
@Transactional
public class ExerciseRecordsTypesServiceImpl extends ServiceImpl<ExerciseRecordsTypesMapper, ExerciseRecordsTypes> implements ExerciseRecordsTypesService{

    @Override
    public void updateExerciseRecordsInBatch(List<ExerciseRecordsTypes> exerciseRecordsTypesList) {
        // 1. 根据exercise_record_id来删除之前的旧数据
        // TODO：考虑到有些exercise_record_id在exercise_records_types表中原来没有数据，因此我们不接收是否删除成功这一返回值
        this.removeByIds(exerciseRecordsTypesList.stream().map(ExerciseRecordsTypes::getExerciseRecordId).distinct().toList());


        //2. 添加新的数据
        boolean success = this.saveBatch(exerciseRecordsTypesList);
        if (!success){
            throw new ExerciseRecordsTypesSaveFailException();
        }
    }
}
