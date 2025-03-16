package sues.xmz.diploma.common.utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * @Description: 密码处理工具类
 * @Author: Zachary Tsu
 * @Date: 2025/3/11 13:13
 */
public class PasswordUtil {

    /**
     * 加密密码
     * @param rawPassword 原始密码
     * @return 加密后的密码
     */
    public static String encodePassword(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    /**
     * 验证密码是否正确
     * @param oldPassword 原始密码
     * @param encodedPassword 加密后的密码
     * @return true - 密码错误，false - 密码正确
     */
    public static boolean verifyNotOldPassword(String oldPassword, String encodedPassword) {
        return !BCrypt.checkpw(oldPassword, encodedPassword);
    }
}