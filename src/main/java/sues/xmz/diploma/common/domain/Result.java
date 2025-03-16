package sues.xmz.diploma.common.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * 统一结果返回类
 * <p>
 * 减少构造方法重载，使用静态工厂方法创建结果对象，统一通过 ResultCode 管理状态码。
 *
 * @param <T>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel. PACKAGE)
public class Result<T> implements Serializable {

    @Schema(description = "状态码 - 不同于Http的响应码")
    private int code;
    @Schema(description = "状态信息")
    private String message;
    @Schema(description = "返回的数据")
    private T data;

    // 成功，有数据
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    // 成功，无数据
    public static <T> Result<T> success() {
        return success(null);
    }

    // 失败，使用 ResultCode
    public static <T> Result<T> failure(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage(), null);
    }

    // 失败，自定义消息01
    public static <T> Result<T> failure(ResultCode resultCode, String message) {
        return new Result<>(resultCode.getCode(), message, null);
    }

    // 失败，自定义消息02
    public static <T> Result<T> failure(int code, String message) {
        return new Result<>(code, message, null);
    }

    // 失败，自定义消息03
    public static <T> Result<T> failure(String message) {
        return Result.failure(ResultCode.BAD_REQUEST, message);
    }

    @Serial
    private static final long serialVersionUID = 1L;
}