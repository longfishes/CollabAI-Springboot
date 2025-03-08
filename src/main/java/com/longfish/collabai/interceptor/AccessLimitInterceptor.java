package com.longfish.collabai.interceptor;

import com.alibaba.fastjson.JSON;
import com.longfish.collabai.annotation.AccessLimit;
import com.longfish.collabai.pojo.Result;
import com.longfish.collabai.service.RedisService;
import com.longfish.collabai.util.IpUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static com.longfish.collabai.constant.CommonConstant.APPLICATION_JSON_UTF8;

@Slf4j
@Component
public class AccessLimitInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisService redisService;

    @Override
    public boolean preHandle(
            @NotNull HttpServletRequest req,
            @NotNull HttpServletResponse resp,
            @NotNull Object handler) throws Exception {
        if (handler instanceof HandlerMethod handlerMethod) {
            AccessLimit accessLimit = handlerMethod.getMethodAnnotation(AccessLimit.class);
            if (accessLimit != null) {
                long seconds = accessLimit.seconds();
                int maxCount = accessLimit.maxCount();
                String key = IpUtil.getIpAddress(req) + "-" + handlerMethod.getMethod().getName();
                try {
                    long q = redisService.incrExpire(key, seconds);
                    if (q > maxCount) {
                        render(resp, Result.error("请求过于频繁，" + seconds + "秒后再试"));
                        log.warn(key + "请求次数超过每" + seconds + "秒" + maxCount + "次");
                        return false;
                    }
                    return true;
                } catch (RedisConnectionFailureException e) {
                    log.warn("redis错误: " + e.getMessage());
                    return false;
                }
            }
        }
        return true;
    }

    private void render(HttpServletResponse response, Result<?> result) throws Exception {
        response.setContentType(APPLICATION_JSON_UTF8);
        OutputStream out = response.getOutputStream();
        String str = JSON.toJSONString(result);
        out.write(str.getBytes(StandardCharsets.UTF_8));
        out.flush();
        out.close();
    }

}
