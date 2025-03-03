package com.longfish.collabai.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusCodeEnum {

    SUCCESS(200, "操作成功", 200),

    FAIL(0, "操作失败", 200),

    NOT_FOUND(404, "not found", 404),

    USERNAME_OR_PASSWORD_ERROR(40001, "用户名或密码错误", 200),

    NO_LOGIN(40002, "用户未登录", 401),

    CODE_ERROR(40003, "验证码错误", 200),

    USER_IS_NULL(40004, "用户名不能为空", 200),

    USER_EXIST(40005, "用户名已存在", 200),

    USER_NOT_EXIST(40006, "用户不存在", 200),

    FORMAT_ERROR(40007, "邮箱或手机格式不正确", 200),

    USERNAME_FORMAT_ERROR(40008, "用户名格式不正确", 200),

    PASSWORD_FORMAT_ERROR(40012, "密码格式不正确", 200),

    DATE_FORMAT_ERROR(40009, "日期格式不正确", 200),

    PHONE_EXIST(40010, "手机号已经绑定", 200),

    EMAIL_EXIST(40011, "邮箱已经绑定", 200),

    AUTHORIZED(40100, "未认证", 401),

    PASSWORD_ERROR(40101, "密码错误", 200),

    START_TIME_ERROR(40102, "开始时间必须在当前时间之后", 200),

    END_TIME_ERROR(40103, "结束时间必须在开始时间之后", 200),

    FORBIDDEN(40300, "没有操作权限", 403),

    METHOD_NOT_ALLOWED(40500, "请求方法不允许", 403),

    SYSTEM_ERROR(50000, "系统异常", 500),

    WEBSOCKET_ERROR(50001, "websocket异常", 500),

    VALID_ERROR(52000, "参数格式不正确", 500);

    private final Integer code;

    private final String desc;

    private final Integer httpStatusCode;

}
