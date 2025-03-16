package sues.xmz.diploma.common.utils;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @Description: 图片工具类
 * @Author: Zachary Tsu
 * @Date: 2025/3/11 13:12
 */
public class ImageUtil {

    /**
     * 将图片字节数组保存到指定文件路径。
     *
     * @param imageBytes 图片字节数组
     * @param filePath   文件路径
     */
    public static void saveImage(byte[] imageBytes, String filePath) {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(imageBytes);
            System.out.println("图片已保存到 " + filePath);
        } catch (IOException e) {
            throw new RuntimeException("保存图片出现问题：" + e, e);
        }
    }
}