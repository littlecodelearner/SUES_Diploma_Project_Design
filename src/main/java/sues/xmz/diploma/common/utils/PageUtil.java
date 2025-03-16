package sues.xmz.diploma.common.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 分页工具类
 * @Author: Zachary Tsu
 * @Date: 2025/3/6 13:07
 */
public final class PageUtil {

    /**
     * 计算MySQL分页查询的偏移量
     *
     * @param current 当前页
     * @param size    每页显示数量
     *
     * @return 偏移量
     */
    public static long countOffset(long current, long size) {
        return (current - 1) * size;
    }

    /**
     * 计算并得到总页数
     * @param total 总数据量
     * @param size 每页展示的数据量
     * @return 总页数
     */
    public static long getTotalPages(long total, long size) {
        if (size == 0) {
            return 0L;
        }
        long pages = total / size;
        if (total % size != 0) {
            pages++;
        }
        return pages;

    }

    /**
     * 判断分页参数是否为空
     * @param current 当前页的页码
     * @param size 每页展示数据量
     */
    public static void pageParamIfNull(Long current, Long size) {
        if (current==null || size==null || current < 1 || size < 1){
            throw new IllegalArgumentException("分页参数不能为null，页码和每页展示量都要大于0");
        }
    }

    /**
     * 从列表中提取从offset开始的size个元素。
     *
     * @param offset 偏移量，即从哪个位置开始提取元素。
     * @param size 需要提取的元素数量。
     * @param list 源列表。
     * @param <T> 列表中元素的类型。
     * @return 提取出的子列表。
     */
    public static <T> List<T> extractElementsFromList(List<T> list, long offset, long size) {
        // 确保offset和size不会导致IndexOutOfBoundsException
        if (offset < 0 || size < 0 || offset >= list.size()) {
            throw new IllegalArgumentException("Invalid offset or size");
        }

        // 计算实际需要提取的元素数量，避免超出列表长度
        long actualSize = Math.min(size, list.size() - offset);

        // 使用subList方法获取子列表
        return new ArrayList<>(list.subList((int) offset, (int) (offset + actualSize)));
    }
}
