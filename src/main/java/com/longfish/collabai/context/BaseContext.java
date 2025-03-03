package com.longfish.collabai.context;


import com.longfish.collabai.enums.StatusCodeEnum;
import com.longfish.collabai.exception.BizException;
import com.longfish.collabai.pojo.entity.User;

public class BaseContext {

    public static ThreadLocal<User> threadLocal = new ThreadLocal<>();

    public static void setCurrent(Long id, String nickname) {
        threadLocal.set(User.builder().id(id).nickname(nickname).build());
    }

    public static Long getCurrentId() {
        if (threadLocal.get() == null) {
            throw new BizException(StatusCodeEnum.NO_LOGIN);
        }
        return threadLocal.get().getId();
    }

    public static String getCurrentName() {
        if (threadLocal.get() == null) {
            throw new BizException(StatusCodeEnum.NO_LOGIN);
        }
        return threadLocal.get().getNickname();
    }

    public static void removeCurrent() {
        threadLocal.remove();
    }

}
