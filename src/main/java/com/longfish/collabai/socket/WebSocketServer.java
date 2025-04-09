package com.longfish.collabai.socket;

import com.longfish.collabai.context.AIStrategyContext;
import com.longfish.collabai.enums.StatusCodeEnum;
import com.longfish.collabai.exception.BizException;
import com.longfish.collabai.pojo.dto.WsDTO;
import com.longfish.collabai.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.annotation.PostConstruct;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.longfish.collabai.constant.CommonConstant.USER_ID;
import static com.longfish.collabai.constant.CommonConstant.USER_NAME;

@Component
@ServerEndpoint(value = "/ws/{sessionId}")
@Slf4j
public class WebSocketServer {

    @Value("${jwt.secret-key}")
    private String secretKey;

    private static final Map<String, WsDTO> sessionMap = new HashMap<>();

    private static String tokenKey;

    private static AIStrategyContext aiStrategyContext;

    @PostConstruct
    public void init() {
        tokenKey = secretKey;
    }

    @Autowired
    public void setAiStrategyContext(AIStrategyContext aiStrategyContext) {
        WebSocketServer.aiStrategyContext = aiStrategyContext;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("sessionId") String sessionId) {
        String token = session.getRequestParameterMap().get("token").get(0);
        long userId;
        String nickname;
        try {
            Claims claims = JwtUtil.parseJWT(tokenKey, token);
            userId = Long.parseLong(claims.get(USER_ID).toString());
            nickname = claims.get(USER_NAME).toString();
        } catch (Exception e) {
            throw new BizException(StatusCodeEnum.NO_LOGIN);
        }

        log.info("会话 {} 建立连接", sessionId);
        WsDTO dto = WsDTO.builder()
                .session(session)
                .nickName(nickname)
                .userId(userId)
                .chatHistory(new ArrayList<>())
                .build();
        sessionMap.put(sessionId, dto);
    }

    @SneakyThrows
    @OnError
    public void onError(Session session, Throwable e) {
        if (e instanceof BizException bizException) {
            session.getBasicRemote().sendText(bizException.getMessage());
        }
        log.error("websocket异常：{}", e.getMessage());
    }

    @OnMessage(maxMessageSize = 104857600)
    public void onMessage(String message, @PathParam("sessionId") String sessionId) {
        log.info(sessionId, ":", message);

        Map<String, String> messageMap = new HashMap<>();
        messageMap.put("role", "user");
        messageMap.put("content", message);

        List<Map<String, String>> chatHistory = sessionMap.get(sessionId).getChatHistory();
        chatHistory.add(messageMap);

        String resp = aiStrategyContext.execChatWithHistory(chatHistory);
        sendMessage(sessionId, resp);

        Map<String, String> respMap = new HashMap<>();
        respMap.put("role", "assistant");
        respMap.put("content", resp);

        chatHistory.add(respMap);
    }

    @OnClose
    public void onClose(@PathParam("sessionId") String sessionId) {
        sessionMap.remove(sessionId);
    }

    public void sendMessage(String sessionId, String message) {
        try {
            sessionMap.get(sessionId).getSession().getBasicRemote().sendText(message);
        } catch (IOException ignore) {}
    }

    public void broadcasts(String message) {
        sessionMap.forEach((s, dto) -> {
            try {
                dto.getSession().getBasicRemote().sendText(message);
            } catch (IOException ignore) {}
        });
    }

}
