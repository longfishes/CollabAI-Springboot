package com.longfish.collabai.interceptor;

import com.longfish.collabai.annotation.NoLogin;
import com.longfish.collabai.context.BaseContext;
import com.longfish.collabai.enums.StatusCodeEnum;
import com.longfish.collabai.exception.BizException;
import com.longfish.collabai.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.longfish.collabai.constant.CommonConstant.USER_ID;
import static com.longfish.collabai.constant.CommonConstant.USER_NAME;

@Component
@Slf4j
public class JwtTokenInterceptor implements HandlerInterceptor {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.token-name}")
    private String tokenName;

    @Override
    public boolean preHandle(
            @NotNull HttpServletRequest req,
            @NotNull HttpServletResponse resp,
            @NotNull Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }
        if (handlerMethod.getBean().getClass().getName().contains("org.springdoc")) {
            return true;
        }
        if (handlerMethod.getMethodAnnotation(NoLogin.class) != null ||
            handlerMethod.getBeanType().getAnnotation(NoLogin.class) != null) {
            BaseContext.setCurrent(-1L, "anonymous");
            return true;
        }
        if (handlerMethod.getBean().getClass().getName().contains("BasicErrorController")) {
            if (resp.getStatus() == 404) throw new BizException(StatusCodeEnum.NOT_FOUND);
            throw new BizException(StatusCodeEnum.FAIL);
        }

        String token = req.getHeader(tokenName);
        try {
            Claims claims = JwtUtil.parseJWT(secretKey, token);
            Long userId = Long.valueOf(claims.get(USER_ID).toString());
            String nickname = claims.get(USER_NAME).toString();
            log.debug("current user: id【{}】 name【{}】", userId, nickname);
            BaseContext.setCurrent(userId, nickname);
            return true;

        } catch (Exception ex) {
            throw new BizException(StatusCodeEnum.NO_LOGIN);
        }
    }
}
