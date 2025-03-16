package sues.xmz.diploma.common.utils;

import cn.hutool.core.util.ReflectUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @Description: 对象转换工具类
 * @Author: Zachary Tsu
 * @Date: 2025/3/11 17:05
 */
public final class BeansConvertUtil {

    /**
     * 将源对象列表转换为包含关联关系的目标对象列表
     *
     * @param firstSourceList    第一个源对象列表
     * @param secondSourceList   第二个源对象列表
     * @param sourceIdGetter     获取源对象主键的方法引用
     * @param targetIdListGetter 获取目标对象关联ID列表的方法引用
     * @param targetType         目标对象类型（必须包含两个参数的构造函数：sourceId, targetId）
     * @param <S1>               第一个源对象类型
     * @param <S2>               第二个源对象类型
     * @param <SOURCE_ID>        源对象ID类型（必须继承Number）
     * @param <TARGET_ID>        目标关联ID类型（必须继承Number）
     * @param <T>                目标对象类型
     *
     * @return 包含所有关联关系的目标对象列表
     */
    public static <S1, S2, SOURCE_ID extends Number, TARGET_ID extends Number, T> List<T> copyToListIncludedBothId(List<S1> firstSourceList,
                                                                                                                   List<S2> secondSourceList,
                                                                                                                   Function<S1, SOURCE_ID> sourceIdGetter,
                                                                                                                   Function<S2, List<TARGET_ID>> targetIdListGetter,
                                                                                                                   Class<T> targetType
    ) {
        Objects.requireNonNull(sourceIdGetter, "源对象的Id获取函数不能为空");
        Objects.requireNonNull(targetIdListGetter, "目标对象的Id获取函数不能为空");
        if (firstSourceList.size() != secondSourceList.size()) {
            throw new IllegalArgumentException("两个源对象列表的大小一致");
        }
        return IntStream.range(0, firstSourceList.size())
                .boxed()
                .flatMap(index -> {
                    SOURCE_ID sourceId = sourceIdGetter.apply(firstSourceList.get(index));
                    if (secondSourceList.isEmpty()) {
                        return Stream.empty();
                    }
                    // 使用自定义的方法获取反射实例
//                    return targetIdList.stream().map(targetId -> this.createInstance(targetType, sourceId, targetId));
                    // 使用Hutool的方法获取反射实例
                    return secondSourceList.stream()
                            .map(secondSource -> ReflectUtil.newInstance(
                                    targetType,
                                        sourceId,
                                        getAllFieldsValues(secondSource))
                            );
                })
                .toList();
    }

    /**
     * 将源对象列表转换为只包含双方Id的对象列表
     * <p>
     * 原始代码参考：
     * <pre>
     * List<HealthGoalsExerciseTypes> healthGoalsExerciseTypesList = IntStream.range(0, healthGoalsList.size())
     *  .boxed()
     *  .flatMap(index -> {
     *      Integer goalId = healthGoalsList.get(index).getGoalId();
     *      List<Integer> exerciseTypeIdList = healthGoalCreateReqList.get(index).getExerciseTypeIdList();
     *      if (exerciseTypeIdList == null || exerciseTypeIdList.isEmpty()) {
     *          return null;
     *      }
     *          return exerciseTypeIdList.stream().map(exerciseTypeId -> new HealthGoalsExerciseTypes(goalId, exerciseTypeId));
     *      })
     *      .toList();
     * </pre>
     *
     * @param firstSourceList    第一个源对象列表
     * @param secondSourceList   第二个源对象列表
     * @param sourceIdGetter     获取源对象主键的方法引用
     * @param targetIdListGetter 获取目标对象关联ID列表的方法引用
     * @param targetType         目标对象类型（必须包含两个参数的构造函数：sourceId, targetId）
     * @param <S1>               第一个源对象类型
     * @param <S2>               第二个源对象类型
     * @param <SOURCE_ID>        源对象ID类型（必须继承Number）
     * @param <TARGET_ID>        目标关联ID类型（必须继承Number）
     * @param <T>                目标对象类型
     *
     * @return 包含所有关联关系的目标对象列表
     */
    public static <S1, S2, SOURCE_ID extends Number, TARGET_ID extends Number, T> List<T> copyToListOnlyHadBothId(List<S1> firstSourceList,
                                                                                                                  List<S2> secondSourceList,
                                                                                                                  Function<S1, SOURCE_ID> sourceIdGetter,
                                                                                                                  Function<S2, List<TARGET_ID>> targetIdListGetter,
                                                                                                                  Class<T> targetType
    ) {
        Objects.requireNonNull(sourceIdGetter, "源对象的Id获取函数不能为空");
        Objects.requireNonNull(targetIdListGetter, "目标对象的Id获取函数不能为空");
        if (firstSourceList.size() != secondSourceList.size()) {
            throw new IllegalArgumentException("两个源对象列表的大小一致");
        }
        return IntStream.range(0, firstSourceList.size())
                .boxed()
                .flatMap(index -> {
                    SOURCE_ID sourceId = sourceIdGetter.apply(firstSourceList.get(index));
                    List<TARGET_ID> targetIdList = targetIdListGetter.apply(secondSourceList.get(index));
                    if (targetIdList == null || targetIdList.isEmpty()) {
                        return Stream.empty();
                    }
                    // 使用自定义的方法获取反射实例
//                    return targetIdList.stream().map(targetId -> this.createInstance(targetType, sourceId, targetId));
                    // 使用Hutool的方法获取反射实例
                    return targetIdList.stream()
                            .map(targetId -> ReflectUtil.newInstance(targetType, sourceId, targetId));
                })
                .toList();
    }

    /**
     * 将源对象列表转换为只包含双方Id的对象列表
     *
     * @param sourceList         源对象列表
     * @param sourceIdGetter     获取源对象主键的方法引用
     * @param targetIdListGetter 获取目标对象关联ID列表的方法引用
     * @param targetType         目标对象类型（必须包含两个参数的构造函数：sourceId, targetId）
     * @param <S>                源对象类型
     * @param <SOURCE_ID>        源对象ID类型（必须继承Number）
     * @param <TARGET_ID>        目标关联ID类型（必须继承Number）
     * @param <T>                目标对象类型
     *
     * @return 包含所有关联关系的目标对象列表
     */
    public static <S, SOURCE_ID extends Number, TARGET_ID extends Number, T> List<T> copyToListOnlyHadBothId(List<S> sourceList,
                                                                                                             Function<S, SOURCE_ID> sourceIdGetter,
                                                                                                             Function<S, List<TARGET_ID>> targetIdListGetter,
                                                                                                             Class<T> targetType
    ) {
        Objects.requireNonNull(sourceIdGetter, "源对象的Id获取函数不能为空");
        Objects.requireNonNull(targetIdListGetter, "目标对象的Id获取函数不能为空");

        return sourceList.stream()
                .flatMap(source -> {
                    SOURCE_ID sourceId = sourceIdGetter.apply(source);
                    List<TARGET_ID> targetIdList = targetIdListGetter.apply(source);
                    if (targetIdList == null || targetIdList.isEmpty()) {
                        return Stream.empty();
                    }
                    // 使用自定义的方法获取反射实例
//                    return targetIdList.stream().map(targetId -> this.createInstance(targetType, sourceId, targetId));
                    // 使用Hutool的方法获取反射实例
                    return targetIdList.stream()
                            .map(targetId -> ReflectUtil.newInstance(targetType, sourceId, targetId));
                })
                .toList();
    }

    /**
     * 通过反射创建实例
     *
     * @param clazz 对象自身的类对象
     * @param args  对象实例化时，所需的参数
     * @param <T>   对象类型
     *
     * @return 实例化的对象
     */
    public static <T> T createInstance(Class<T> clazz, Object... args) {
        try {
            Constructor<T> constructor = clazz.getConstructor(
                    args[0].getClass(),
                    args[1].getClass()
            );
            return constructor.newInstance(args);
        } catch (Exception e) {
            throw new IllegalArgumentException("无法创建实例: " + clazz.getName(), e);
        }
    }

    public static Object[] getAllFieldsValues(Object obj) {
        // 获取对象所属类的所有声明字段，包括私有字段
        Field[] fields = obj.getClass().getDeclaredFields();
        Object[] values = new Object[fields.length];

        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            // 设置为可访问，以便访问私有字段
            field.setAccessible(true);
            try {
                values[i] = field.get(obj);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("对象相关字段获取失败" + e, e);
            }
        }

        return values;
    }
}
