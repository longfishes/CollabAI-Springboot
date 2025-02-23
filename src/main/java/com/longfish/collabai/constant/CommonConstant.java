package com.longfish.collabai.constant;

public interface CommonConstant {

    String PATTERN = "yyyy-MM-dd HH:mm:ss";

    String DATE_PATTERN = "yyyy-MM-dd";

    String CODE = "CODE";

    String USER_ID = "userId";

    String UNKNOWN = "未知";

    String APPLICATION_JSON_UTF8 = "application/json;charset=utf-8";

    String APPLICATION_JSON = "application/json";

    String TEXT_STREAM = "text/event-stream";

    String USERNAME_CHECK_REGEX = "^(?!\\d+$)[a-zA-Z0-9_]{2,49}$";

    String PASSWORD_CHECK_REGEX = "^(?!\\d+$)[a-zA-Z0-9_@#$%^&*!]{6,18}$";

    String PRE_TAG = "<mark>";

    String POST_TAG = "</mark>";

    String TOKEN_NAME = "Authorization";

    String ID_NAME = "uid";

    String HEADER_ADVICE = "登录后返回jwt令牌，之后所有请求请携带此参数";

    String HEADER_VAR = "{{token}}";

}
