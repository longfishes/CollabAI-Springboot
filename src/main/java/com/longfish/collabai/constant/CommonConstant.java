package com.longfish.collabai.constant;

public interface CommonConstant {

    String PATTERN = "yyyy-MM-dd HH:mm:ss";

    String MEETING_PATTERN = "yyyy-MM-dd HH:mm";

    String DATE_PATTERN = "yyyy-MM-dd";

    String USER_ID = "userId";

    String USER_NAME = "userName";

    String UNKNOWN = "未知";

    String APPLICATION_JSON_UTF8 = "application/json;charset=utf-8";

    String APPLICATION_JSON = "application/json";

    String TEXT_STREAM = "text/event-stream";

    String USERNAME_CHECK_REGEX = "^(?!\\d+$)[a-zA-Z0-9_]{2,49}$";

    String PASSWORD_CHECK_REGEX = "^(?!\\d+$)[a-zA-Z0-9_@#$%^&*!]{6,18}$";

    String PRE_TAG = "<mark>";

    String POST_TAG = "</mark>";

    String TOKEN_NAME = "Authorization";

    String HEADER_ADVICE = "登录后返回jwt令牌，之后所有请求请携带此参数";

    String HEADER_VAR = "{{token}}";

    String DEFAULT_AVATAR = "https://blog.frium.top/upload/mswlm.jpeg";

    String DEFAULT_COVER = "https://blog.frium.top/upload/%E7%BD%91%E6%98%93%E4%BA%91%E8%83%8C%E6%99%AF.jpg";

    String FINISH_TAG = "@FINISH@";

}
