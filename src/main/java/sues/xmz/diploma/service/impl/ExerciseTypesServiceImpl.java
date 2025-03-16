package sues.xmz.diploma.service.impl;

import com.baomidou.mybatisplus.core.batch.MybatisBatch;
import com.baomidou.mybatisplus.core.toolkit.MybatisBatchUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import sues.xmz.diploma.common.domain.PageResponse;
import sues.xmz.diploma.common.exception.exercise_types.ExerciseTypesNotFoundException;
import sues.xmz.diploma.domain.po.ExerciseTypes;
import sues.xmz.diploma.domain.req.exercise_types.ExerciseTypesCreateReq;
import sues.xmz.diploma.domain.req.exercise_types.ExerciseTypesUpdateReq;
import sues.xmz.diploma.mapper.ExerciseTypesMapper;
import sues.xmz.diploma.service.ExerciseTypesService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ExerciseTypesServiceImpl extends ServiceImpl<ExerciseTypesMapper, ExerciseTypes> implements ExerciseTypesService {

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private SqlSessionFactory sqlSessionFactory;

    @Override
    public List<BatchResult> addExerciseTypesInBatch(ExerciseTypesCreateReq exerciseTypesCreateReq) {

        // 去重，并准备数据
        List<ExerciseTypes> exerciseTypesList = exerciseTypesCreateReq.getExerciseNameList()
                .stream()
                .distinct()
                .map(ExerciseTypes::new)
                .toList();

        /*
         TODO：通过MybatisBatch的execute方法，并且自定义带注解的mapper方法，来实现批量插入非实体类型数据到exercise_types表
         */
        MybatisBatch<ExerciseTypes> mybatisBatch = new MybatisBatch<>(sqlSessionFactory, exerciseTypesList);
        MybatisBatch.Method<ExerciseTypes> method = new MybatisBatch.Method<>(ExerciseTypesMapper.class);
        return mybatisBatch.execute(method.get("batchInsertExerciseTypes"
                , (exerciseTypes) -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("exerciseTypesData", exerciseTypes);
                    return map;
                }));
    }

    @Override
    public List<BatchResult> updateExerciseTypesInBatch(List<ExerciseTypesUpdateReq> exerciseTypesUpdateReqList) {
        // 准备数据，并检查是否存在
        List<ExerciseTypes> exerciseTypesList = exerciseTypesUpdateReqList.stream()
                .map(data -> new ExerciseTypes(data.getExerciseTypeId(), data.getNewExerciseTypeName()))
                .toList();
        if (exerciseTypesList.isEmpty()) {
            throw new ExerciseTypesNotFoundException();
        }

        /*
        TODO：通过MybatisBatchUtils的execute方法和Spring事务处理的方式，执行批量修改操作
         */
        return transactionTemplate.execute(status -> {
            MybatisBatch.Method<ExerciseTypes> mapperMethod = new MybatisBatch.Method<>(ExerciseTypesMapper.class);
            // 执行批量插入
            return MybatisBatchUtils.execute(
                    sqlSessionFactory,
                    exerciseTypesList,
                    mapperMethod.update(exerciseTypes ->
                            Wrappers.<ExerciseTypes>lambdaUpdate()
                                    .eq(ExerciseTypes::getExerciseTypeId, exerciseTypes.getExerciseTypeId())
                                    .set(ExerciseTypes::getExerciseName, exerciseTypes.getExerciseName())
                    ));
        });

    }


    @Override
    public PageResponse<ExerciseTypes> listExerciseTypesByPage(Long current, Long size, String name) {
        return PageResponse.getPageResponse(
                this.page(Page.of(current, size),
                        Wrappers.<ExerciseTypes>lambdaQuery()
                                .like(name != null && !name.isBlank(), ExerciseTypes::getExerciseName, name)
                )
        );
    }
}
