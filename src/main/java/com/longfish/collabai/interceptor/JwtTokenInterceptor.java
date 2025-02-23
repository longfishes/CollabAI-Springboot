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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.longfish.collabai.constant.CommonConstant.USER_ID;

@Component
@Slf4j
public class JwtTokenInterceptor implements HandlerInterceptor {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.token-name}")
    private String tokenName;

    @Override
    @SuppressWarnings("all")
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        if (((HandlerMethod) handler).getBean().getClass().getName().contains("org.springdoc")) {
            return true;
        }
        if (((HandlerMethod) handler).getMethodAnnotation(NoLogin.class) != null) {
            return true;
        }

        String token = req.getHeader(tokenName);
        try {
            Claims claims = JwtUtil.parseJWT(secretKey, token);
            Long userId = Long.valueOf(claims.get(USER_ID).toString());
            log.info("current id: {}", userId);
            BaseContext.setCurrentId(userId);
            return true;

        } catch (Exception ex) {
            throw new BizException(StatusCodeEnum.NO_LOGIN);
        }
    }
}
