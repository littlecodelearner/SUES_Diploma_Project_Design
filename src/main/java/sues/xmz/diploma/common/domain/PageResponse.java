package sues.xmz.diploma.common.domain;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import sues.xmz.diploma.common.utils.PageUtil;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Schema(description = "分页查询结果")
public class PageResponse<T> implements Serializable {

    @Schema(minimum = "1", description = "当前页码。默认从1开始")
    private Long current;

    @Schema(minimum = "1", description = "每页展示的数据量，不能小于1")
    private Long size;

    @Schema(description = "总页数")
    private Long pages;

    @Schema(description = "总记录数（总数据量）")
    private Long total;

    @Schema(description = "是否有上一页")
    private Boolean hasPrevious;

    @Schema(description = "是否有下一页")
    private Boolean hasNext;

    @Schema(description = "查询到的所有数据")
    private List<T> dataList = Collections.emptyList();

    public static <T> PageResponse<T> empty() {
        return PageResponse.<T>builder()
                .current(0L)
                .size(0L)
                .pages(0L)
                .total(0L)
                .hasPrevious(false)
                .hasNext(false)
                .dataList(Collections.emptyList())
                .build();
    }

    /**
     * 将Mybatis-plus分页结果转换为PageResponse对象
     *
     * @param pageResult Mybatis-plus分页结果
     * @param current    当前页的页码
     * @param size       每页的数据展示量
     * @param total      总数据量
     * @param <T>        查询数据的对象类型
     *
     * @return PageResponse对象
     */

    public static <T> PageResponse<T> getPageResponse(List<T> pageResult, long current, long size, long total) {
        long pages = PageUtil.getTotalPages(total, size);
        return PageResponse.<T>builder()
                .current(current)
                .size(size)
                .total(total)
                .pages(pages)
                .hasPrevious(current > 1)
                .hasNext(current < pages)
                .dataList(pageResult)
                .build();
    }

    /**
     * 将Mybatis-plus分页结果转换为PageResponse对象
     *
     * @param pageResult Mybatis-plus分页结果
     * @param <T>        转换目标类的对象类型
     *
     * @return PageResponse对象
     */
    public static <T> PageResponse<T> getPageResponse(Page<T> pageResult) {
        return PageResponse.<T>builder()
                .current(pageResult.getCurrent())
                .size(pageResult.getSize())
                .total(pageResult.getTotal())
                .pages(pageResult.getPages())
                .hasPrevious(pageResult.hasPrevious())
                .hasNext(pageResult.hasNext())
                .dataList(pageResult.getRecords())
                .build();
    }

    /**
     * 将Mybatis-plus分页结果转换为PageResponse对象
     *
     * @param pageResult  Mybatis-plus分页结果
     * @param targetClass 转换目标类
     * @param <S>         被转换源类的对象类型
     * @param <T>         转换目标类的对象类型
     *
     * @return PageResponse对象
     */
    public static <S, T> PageResponse<T> getPageResponse(Page<S> pageResult, Class<T> targetClass) {
        return PageResponse.<T>builder()
                .current(pageResult.getCurrent())
                .size(pageResult.getSize())
                .total(pageResult.getTotal())
                .pages(pageResult.getPages())
                .hasPrevious(pageResult.hasPrevious())
                .hasNext(pageResult.hasNext())
                .dataList(pageResult.getRecords().stream().map(record -> BeanUtil.copyProperties(record, targetClass)).toList())
                .build();
    }

    @Serial
    private static final long serialVersionUID = 1L;
}
