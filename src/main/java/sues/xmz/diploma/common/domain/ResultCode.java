package sues.xmz.diploma.common.domain;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 常用状态码枚举
 */
@Getter
public enum ResultCode implements Serializable {
    SUCCESS(200, "成功"),
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    BAD_REQUEST(400, "请求参数错误"),
    NOT_FOUND(404, "资源未找到"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    // 其他常用状态码
    USER_NOT_FOUND(1000, "用户不存在"),
    PASSWORD_ERROR(1001, "密码输入错误"),
    USERNAME_ALREADY_EXISTS(1002, "用户账号已存在"),
    EMAIL_ALREADY_EXISTS(1003, "邮箱已经被注册"),
    INCORRECT_OLD_PASSWORD(1004,"旧密码输入错误"),
    HEALTH_PROFILE_NOT_FOUND(1005,"健康档案没有找到"),
    ONLY_ONE_HEALTH_PROFILE_PER_USER(1006,"每个用户只能创建且拥有一个健康档案"),
    DIET_RECORDS_NOT_FOUND(1007,"没找到饮食记录"),
    HEALTH_DATA_NOT_FOUND(1008, "健康数据没找到"),
    EXERCISE_TYPES_NOT_FOUND(1009,"请求数据中没找到运动类型等数据" ),
    EXERCISE_RECORD_NOT_FOUND(1010,"运动记录没找到" ),
    EXERCISE_RECORD_CREATE_FAIL(1011,"运动记录批量保存失败" ),
    EXERCISE_RECORD_TYPE_SAVE_FAIL(1012,"运动记录与类型之间的联系保存失败"),
    EXERCISE_RECORD_TYPE_UPDATE_FAIL(1013,"运动记录与类型之间的联系更新失败"),
    HEALTH_GOAL_CREATE_FAILURE(1014, "健康目标计划创建失败"),
    HEALTH_GOAL_UPDATE_FAILURE(1015, "健康目标计划更新失败"),
    USERS_DETAIL_QUERY_FAILURE(1016, "分页查询所有用户的详细数据失败"),
    USER_INFO_UPDATE_FAILED(1017, "更新用户信息失败"),
    DIET_RECORDS_SAVE_FAILURE(1018, "添加新的饮食记录失败"),
    DIET_RECORDS_FOODS_SAVE_FAILURE(1019, "添加新的【饮食记录表与食物表之间的联系】的数据失败"),
    DIET_RECORDS_UPDATE_FAILURE(1020, "修改现有的饮食记录失败"),
    DIET_RECORDS_FOODS_UPDATE_FAILURE(1021, "请检查传入参数是否正确，修改现有的【饮食记录表与食物表之间的联系】的数据失败"),
    DIET_RECORDS_DELETE_FAILURE(1022, "删除饮食记录失败"),
    EXERCISE_TYPES_DELETE_FAILURE(1023, "删除运动类型失败"),
    EXERCISE_TYPES_QUERY_FAILURE(1024, "查看运动类型失败"),
    DIET_FOODS_SAVE_FAILURE(1025, "批量增加食物数据失败"),
    DIET_FOODS_UPDATE_FAILURE(1026, "批量修改食物数据失败"),
    DIET_FOODS_DELETE_FAILURE(1027, "批量删除食物数据失败"),
    EXERCISE_TYPES_CREATE_FAILURE(1028, "添加新的运动类型失败"),
    EXERCISE_TYPES_UPDATE_FAILURE(1029, "修改运动类型失败"),
    EXERCISE_RECORD_TYPE_DELETE_FAIL(1029, "删除【运动记录与运动类型之间的联系】的数据失败"),
    EXERCISE_RECORDS_DELETE_FAIL(1030, "运动记录删除失败，删除的运动记录已经不存在"),
    USER_REGISTER_FAILED(1031, "用户注册失败"),
    PASSWORD_CHANGE_FAILURE(1032, "密码修改失败"),
    DIET_RECORDS_FOODS_DELETE_FAILURE(1033, "删除现有的【饮食记录表与食物表之间的联系】的数据失败"),
    HEIGHT_AND_WEIGHT_NOT_FOUND_FAILURE(1034, "身高和体重数据未找到，无法进行BMI值的计算"),
    USER_HEIGHT_DATA_NOT_FOUND_FAILURE(1035, "用户的身高数据未找到，无法进行BMI值的计算"),
    HEALTH_DATA_CREATE_FAILURE(1036, "添加新的健康数据记录失败"),
    HEALTH_DATA_UPDATE_FAILURE(1037, "修改现有的健康数据记录失败"),
    HEALTH_DATA_DELETE_FAILURE(1038, "删除指定的健康数据记录失败"),
    HEALTH_GOALS_EXERCISE_TYPES_SAVE_FAILED(1039, "添加新的【健康目标与运动类型之间的联系】的数据失败"),
    HEALTH_GOALS_EXERCISE_TYPES_DELETE_FAILED(1040, "删除现有的【健康目标与运动类型之间的联系】的数据失败"),
    HEALTH_GOAL_DELETE_FAILURE(1041, "删除健康目标数据失败"),
    HEALTH_PROFILE_CREATE_FAILED(1042, "创建健康档案失败"),
    HEALTH_PROFILE_UPDATE_FAILED(1043, "更新健康档案失败"),
    HEALTH_PROFILE_DELETE_FAILED(1044, "删除健康档案失败"),
    USERS_DELETE_FAILURE(1045, "删除用户账号失败"),
    ;

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Serial
    private static final long serialVersionUID = 1L;

}