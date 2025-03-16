package sues.xmz.diploma.common.utils;

import sues.xmz.diploma.common.exception.health_data.HeightAndWeightNotFoundException;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @Description: 用户相关数据计算工具类
 * @Author: Zachary Tsu
 * @Date: 2025/3/11 13:13
 */
public final class UserDataCalculationUtil {

    /**
     * 计算BMI值
     * @param height 身高（单位：cm）
     * @param weight 体重（单位：kg）
     * @return BMI值
     */
    public static BigDecimal calculateBMI(BigDecimal height, BigDecimal weight) {

        // 检查身高和体重是否大于0
        if (height.compareTo(BigDecimal.ZERO) <= 0 || weight.compareTo(BigDecimal.ZERO) <= 0) {
            throw new HeightAndWeightNotFoundException();
        }

        // 将身高从厘米转换为米
        BigDecimal heightInMeters = height.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);

        // 计算身高的平方
        BigDecimal heightSquared = heightInMeters.multiply(heightInMeters);

        // 计算并返回BMI指数
        return weight.divide(heightSquared, 2, RoundingMode.HALF_UP);
    }
}
