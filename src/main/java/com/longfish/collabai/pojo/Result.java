package com.longfish.collabai.pojo;

import com.longfish.collabai.enums.StatusCodeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 后端统一返回结果
 * @param <T>
 */
@Data
public class Result<T> implements Serializable {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "200")
    private Integer code;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "操作成功")
    private String msg;

    private T data;

    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.code = StatusCodeEnum.SUCCESS.getCode();
        result.msg = StatusCodeEnum.SUCCESS.getDesc();
        return result;
    }

    public static <T> Result<T> success(T object) {
        Result<T> result = new Result<>();
        result.code = StatusCodeEnum.SUCCESS.getCode();
        result.msg = StatusCodeEnum.SUCCESS.getDesc();
        result.data = object;
        return result;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.msg = msg;
        result.code = StatusCodeEnum.FAIL.getCode();
        return result;
    }

    public static <T> Result<T> error(StatusCodeEnum statusCodeEnum) {
        Result<T> result = new Result<>();
        result.msg = statusCodeEnum.getDesc();
        result.code = statusCodeEnum.getCode();
        return result;
    }

}
