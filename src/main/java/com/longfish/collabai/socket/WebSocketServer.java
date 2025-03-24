package com.longfish.collabai.socket;

import com.alibaba.fastjson.JSON;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static com.longfish.collabai.constant.CommonConstant.USER_ID;
import static com.longfish.collabai.constant.CommonConstant.USER_NAME;

@Component
@ServerEndpoint(value = "/ws/{meetingId}/{sessionId}")
@Slf4j
public class WebSocketServer {

    @Value("${jwt.secret-key}")
    private String secretKey;

    private static final Map<String, WsDTO> sessionMap = new HashMap<>();

    private static String tokenKey;

    @PostConstruct
    public void init() {
        tokenKey = secretKey;
    }

    @OnOpen
    public void onOpen(
            Session session,
            @PathParam("meetingId") String meetingId,
            @PathParam("sessionId") String sessionId) {

        String token = session.getRequestParameterMap().get("token").get(0);
        Long userId = null;
        String nickname = null;
        try {
            Claims claims = JwtUtil.parseJWT(tokenKey, token);
            userId = Long.valueOf(claims.get(USER_ID).toString());
            nickname = claims.get(USER_NAME).toString();
        } catch (Exception e) {
            throw new BizException(StatusCodeEnum.NO_LOGIN);
        }

        log.info("会话 {} 会议号 {} 建立连接", sessionId, meetingId);
        WsDTO dto = WsDTO.builder()
                .session(session)
                .meetingId(meetingId)
                .nickName(nickname)
                .userId(userId)
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
    public void onMessage(
            byte[] data,
            @PathParam("meetingId") String meetingId,
            @PathParam("sessionId") String sessionId) {
        sendMessage(sessionId, meetingId, data);
    }

    @OnClose
    public void onClose(@PathParam("sessionId") String sessionId) {
        sessionMap.remove(sessionId);
    }

    public void sendMessage(String fromSessionId, String meetingId, byte[] data) {
        sessionMap.forEach((s, wsDTO) -> {
            try {
                if (s.equals(fromSessionId)) return;
                if (!wsDTO.getMeetingId().equals(meetingId)) return;

                Map<String, String> map = new HashMap<>();
                String encodeData = new String(Base64.getEncoder().encode(data));
                map.put("data", encodeData);
                map.put("userId", wsDTO.getUserId().toString());
                map.put("nickName", wsDTO.getNickName());

                wsDTO.getSession().getBasicRemote().sendText(JSON.toJSONString(map));
            } catch (IOException ignore) {}
        });
    }

    public void broadcasts(String message) {
        sessionMap.forEach((s, dto) -> {
            try {
                dto.getSession().getBasicRemote().sendText(message);
            } catch (IOException ignore) {}
        });
    }

}
