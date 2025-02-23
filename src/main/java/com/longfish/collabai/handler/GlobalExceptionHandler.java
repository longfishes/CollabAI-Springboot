package com.longfish.collabai.handler;

import com.longfish.collabai.enums.StatusCodeEnum;
import com.longfish.collabai.exception.BizException;
import com.longfish.collabai.pojo.Result;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @Autowired
    private HttpServletResponse response;

    @ExceptionHandler
    public Result bizExceptionHandler(BizException ex) {
        log.info("业务异常信息：{}", ex.getMessage());
        if (ex.getStatusCodeEnum() != null) {
            response.setStatus(ex.getHttpStatusCode());
            return Result.error(ex.getStatusCodeEnum());
        }
        response.setStatus(ex.getHttpStatusCode());
        return Result.error(ex.getMessage());
    }

    @ExceptionHandler
    public Result noHandlerFoundExceptionHandler(NoHandlerFoundException ex) {
        log.error("不存在的路径, {}", ex.getMessage());
        response.setStatus(StatusCodeEnum.NOT_FOUND.getHttpStatusCode());
        return Result.error(StatusCodeEnum.NOT_FOUND);
    }

    @ExceptionHandler
    public Result formatExceptionHandler(HttpMessageNotReadableException ex) {
        log.error("参数格式错误, {}", ex.getMessage());
        response.setStatus(StatusCodeEnum.VALID_ERROR.getHttpStatusCode());
        return Result.error(StatusCodeEnum.VALID_ERROR);
    }

    @ExceptionHandler
    public Result formatExceptionHandler(HttpMediaTypeNotSupportedException ex) {
        log.error("参数格式错误, {}", ex.getMessage());
        response.setStatus(StatusCodeEnum.VALID_ERROR.getHttpStatusCode());
        return Result.error(StatusCodeEnum.VALID_ERROR);
    }

    @ExceptionHandler
    public Result httpRequestMethodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException ex) {
        log.error("请求方法不允许, {}", ex.getMessage());
        response.setStatus(StatusCodeEnum.METHOD_NOT_ALLOWED.getHttpStatusCode());
        return Result.error(StatusCodeEnum.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler
    public Result exceptionHandler(Exception ex) {
        log.error("异常信息：{}", ex.getMessage());
        ex.printStackTrace();
        response.setStatus(StatusCodeEnum.SYSTEM_ERROR.getHttpStatusCode());
        return Result.error(StatusCodeEnum.SYSTEM_ERROR);
    }
}
