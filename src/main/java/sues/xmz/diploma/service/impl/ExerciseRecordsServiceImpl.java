package sues.xmz.diploma.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sues.xmz.diploma.common.domain.PageResponse;
import sues.xmz.diploma.common.exception.exercise_records.ExerciseRecordCreateFailException;
import sues.xmz.diploma.common.exception.exercise_records.ExerciseRecordNotFoundException;
import sues.xmz.diploma.common.exception.exercise_records_types.ExerciseRecordsTypesSaveFailException;
import sues.xmz.diploma.common.exception.exercise_records_types.ExerciseRecordsTypesUpdateFailException;
import sues.xmz.diploma.common.utils.PageUtil;
import sues.xmz.diploma.domain.dto.exercise_records_types.ExerciseRecordsDTO;
import sues.xmz.diploma.domain.po.ExerciseRecords;
import sues.xmz.diploma.domain.po.ExerciseRecordsTypes;
import sues.xmz.diploma.domain.req.exercise_records.ExerciseRecordUpdateReq;
import sues.xmz.diploma.domain.req.exercise_records.ExerciseRecordsCreateReq;
import sues.xmz.diploma.domain.req.exercise_records.ExerciseRecordsQueryReq;
import sues.xmz.diploma.mapper.ExerciseRecordsMapper;
import sues.xmz.diploma.service.ExerciseRecordsService;
import sues.xmz.diploma.service.ExerciseRecordsTypesService;

import java.util.List;
import java.util.stream.IntStream;

@Service
@Transactional
public class ExerciseRecordsServiceImpl extends ServiceImpl<ExerciseRecordsMapper, ExerciseRecords> implements ExerciseRecordsService{

    @Resource
    private ExerciseRecordsMapper exerciseRecordsMapper;

    @Resource
    private ExerciseRecordsTypesService exerciseRecordsTypesService;

    @Override
    public void saveExerciseRecordInBatch(List<ExerciseRecordsCreateReq> exerciseRecordsCreateReqList) {
        // 1.保存数据exercise_recode表
        List<ExerciseRecords> exerciseRecordsList = BeanUtil.copyToList(exerciseRecordsCreateReqList, ExerciseRecords.class);
        boolean success = this.saveBatch(exerciseRecordsList);
        // 判断是否保存成功
        if (!success) {
            throw new ExerciseRecordCreateFailException();
        }

        // 2.保存数据到exercise_record_type表
        List<ExerciseRecordsTypes> exerciseRecordsTypesList = IntStream.range(0, exerciseRecordsList.size())
                .boxed()
                .flatMap(i -> {
                    ExerciseRecords record = exerciseRecordsList.get(i);
                    List<Integer> typeIds = exerciseRecordsCreateReqList.get(i).getExerciseTypeIdList();
                    return typeIds.stream()
                            .map(typeId -> new ExerciseRecordsTypes(record.getExerciseRecordId(), typeId));
                })
                .toList();

        if (exerciseRecordsTypesList.isEmpty()){
            return;
        }

        success = exerciseRecordsTypesService.saveBatch(exerciseRecordsTypesList);
        if (!success) {
            throw new ExerciseRecordsTypesSaveFailException();
        }

    }

    @Override
    public void updateExerciseRecordInBatch(List<ExerciseRecordUpdateReq> exerciseRecordUpdateReqList) {
        // 1.更新exercise_recode表的数据
        boolean success = this.updateBatchById(BeanUtil.copyToList(exerciseRecordUpdateReqList,ExerciseRecords.class));
        // 判断是否更新成功
        if (!success) {
            throw new ExerciseRecordNotFoundException();
        }

        // 2.更新exercise_record_type表的数据
        List<ExerciseRecordsTypes> exerciseRecordsTypesList = IntStream.range(0, exerciseRecordUpdateReqList.size())
                .boxed()
                .flatMap(index -> {
                    Integer exerciseRecordId = exerciseRecordUpdateReqList.get(index).getExerciseRecordId();
                    List<Integer> exerciseTypeIdList = exerciseRecordUpdateReqList.get(index).getExerciseTypeIdList();
                    return exerciseTypeIdList.stream().map(exerciseTypeId -> new ExerciseRecordsTypes(exerciseRecordId, exerciseTypeId));
                })
                .toList();

        if (exerciseRecordUpdateReqList.isEmpty()){
            return;
        }

        try {
            exerciseRecordsTypesService.updateExerciseRecordsInBatch(exerciseRecordsTypesList);
        } catch (Exception e) {
            throw new ExerciseRecordsTypesUpdateFailException();
        }

    }

    /**
     * @Description:
     * TODO 需要注意的事情：
     *  <pre>
     *  1. 为了获取运动记录数据，SQL语句要使用外连接来确保即使运动类型是空的，我们也依然能够获取运动记录数据，以此来避免一些没有运动类型的运动记录数据被忽略。
     *  2. 即使这样，我们依然不能在前端获取真正意义上的30条运动记录数据，因为每个运动记录数据中又包含多个运动类型数据。
     *  - 举个例子：假如我们想要获取30个运动记录数据，但是可能实际上我们只获得了29个，因为有一个运动记录中包含了2个运动类型数据（其它28个运动记录都只包含1个运动类型数据），因此我们就少了1个运动记录
     *  </pre>
     * @Author: Zachary Tsu
     * @Date: 2025/3/13 20:20
     */
    @Override
    public PageResponse<ExerciseRecordsDTO> listPaginatedExerciseRecordsByTimeRange(ExerciseRecordsQueryReq exerciseRecordsQueryReq) {
        PageUtil.pageParamIfNull(exerciseRecordsQueryReq.getCurrent(), exerciseRecordsQueryReq.getSize());

        List<ExerciseRecordsDTO> exerciseRecordsDTOList = exerciseRecordsMapper.listPaginatedExerciseRecordsDetailsByTimeRange(
                exerciseRecordsQueryReq,
                PageUtil.countOffset(exerciseRecordsQueryReq.getCurrent(), exerciseRecordsQueryReq.getSize())
        );
        return PageResponse.getPageResponse(

                exerciseRecordsDTOList,
                exerciseRecordsQueryReq.getCurrent(),
                exerciseRecordsQueryReq.getSize(),
                exerciseRecordsMapper.listAllExerciseRecordsByTimeRange(exerciseRecordsQueryReq).size()
        );
    }


}
