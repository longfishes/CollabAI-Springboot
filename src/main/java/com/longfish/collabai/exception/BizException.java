package com.longfish.collabai.exception;


import com.longfish.collabai.enums.StatusCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BizException extends RuntimeException {

    private StatusCodeEnum statusCodeEnum;

    private final Integer code;

    private final String message;

    private final Integer httpStatusCode;

    public BizException(StatusCodeEnum statusCodeEnum) {
        this.statusCodeEnum = statusCodeEnum;
        this.code = statusCodeEnum.getCode();
        this.message = statusCodeEnum.getDesc();
        this.httpStatusCode = statusCodeEnum.getHttpStatusCode();
    }

    public BizException(String message) {
        this.code = StatusCodeEnum.FAIL.getCode();
        this.message = message;
        this.httpStatusCode = StatusCodeEnum.FAIL.getHttpStatusCode();
    }

}
