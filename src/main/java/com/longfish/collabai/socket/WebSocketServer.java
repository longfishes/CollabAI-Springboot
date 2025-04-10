package com.longfish.collabai.socket;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.longfish.collabai.context.AIStrategyContext;
import com.longfish.collabai.enums.StatusCodeEnum;
import com.longfish.collabai.exception.BizException;
import com.longfish.collabai.pojo.dto.WsDTO;
import com.longfish.collabai.pojo.entity.Meeting;
import com.longfish.collabai.service.IMeetingService;
import com.longfish.collabai.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.longfish.collabai.constant.CommonConstant.*;

@Component
@ServerEndpoint(value = "/ws/{meetingId}/{sessionId}")
@Slf4j
public class WebSocketServer {

    @Value("${jwt.secret-key}")
    private String secretKey;

    private static final Map<String, WsDTO> sessionMap = new ConcurrentHashMap<>();

    private static String tokenKey;

    private static AIStrategyContext aiStrategyContext;

    private static IMeetingService meetingService;

    private static final int MAX_CHAT_HISTORY = 100;

    @PostConstruct
    public void init() {
        tokenKey = secretKey;
    }

    @Autowired
    public void setAiStrategyContext(AIStrategyContext aiStrategyContext) {
        WebSocketServer.aiStrategyContext = aiStrategyContext;
    }

    @Autowired
    public void setMeetingService(IMeetingService meetingService) {
        WebSocketServer.meetingService = meetingService;
    }

    @OnOpen
    public void onOpen(Session session,
            @PathParam("sessionId") String sessionId,
            @PathParam("meetingId") String meetingId) {
        long userId;
        String nickname;
        try {
            String token = session.getRequestParameterMap().get("token").get(0);
            Claims claims = JwtUtil.parseJWT(tokenKey, token);
            userId = Long.parseLong(claims.get(USER_ID).toString());
            nickname = claims.get(USER_NAME).toString();
        } catch (Exception e) {
            throw new BizException(StatusCodeEnum.NO_LOGIN);
        }

        List<Map<String, String>> chatHistory = new ArrayList<>();
        try {
            Meeting meeting = meetingService.getById(meetingId);
            if (meeting == null) throw new BizException(StatusCodeEnum.MEETING_NOT_FOUND);

            String summarizeContent =  "帮我条理清晰地总结以下内容：\n" +
                    "会议主题：" + meeting.getTitle() +
                    "\n会议文档：" + meeting.getMdContent() +
                    "\n会议录音详细记录：" + meeting.getSpeechText();

            String aiSummary = meeting.getAiSummary();
            if (StringUtils.isBlank(aiSummary)) aiSummary = "空";

            Map<String, String> summarizeContentMap = new HashMap<>();
            summarizeContentMap.put("role", "user");
            summarizeContentMap.put("content", summarizeContent);

            Map<String, String> aiSummaryMap = new HashMap<>();
            aiSummaryMap.put("role", "assistant");
            aiSummaryMap.put("content", aiSummary);

            chatHistory.add(summarizeContentMap);
            chatHistory.add(aiSummaryMap);

        } catch (Exception ignore) {}

        log.info("会话 {} 建立连接", sessionId);
        WsDTO dto = WsDTO.builder()
                .session(session)
                .nickName(nickname)
                .userId(userId)
                .chatHistory(chatHistory)
                .build();
        sessionMap.put(sessionId, dto);
    }

    @SneakyThrows
    @OnError
    public void onError(Session session, Throwable e, @PathParam("sessionId") String sessionId) {
        log.debug("websocket异常：{}", e.getMessage());

        if (e instanceof BizException bizException) {
            session.getBasicRemote().sendText(bizException.getMessage());
        }

        try {
            if (sessionId != null) {
                sessionMap.remove(sessionId);
            }
            if (session.isOpen()) {
                session.close();
            }
        } catch (IOException ignore) {}
    }

    @OnMessage(maxMessageSize = 104857600)
    public void onMessage(String message, @PathParam("sessionId") String sessionId) {
        if (HEART_BREAK.equals(message)) return;

        Map<String, String> messageMap = new HashMap<>();
        messageMap.put("role", "user");
        messageMap.put("content", message);

        addChatHistory(sessionId, messageMap);

        String resp = aiStrategyContext.execChatStream(
                sessionMap.get(sessionId).getSession(),
                sessionMap.get(sessionId).getChatHistory()
        );

        Map<String, String> respMap = new HashMap<>();
        respMap.put("role", "assistant");
        respMap.put("content", resp);

        addChatHistory(sessionId, respMap);
    }

    @OnClose
    public void onClose(@PathParam("sessionId") String sessionId) {
        sessionMap.remove(sessionId);
    }

    public void broadcasts(String message) {
        sessionMap.forEach((s, dto) -> {
            try {
                dto.getSession().getBasicRemote().sendText(message);
            } catch (IOException ignore) {}
        });
    }

    @Scheduled(fixedRate = 300000)
    public void cleanupSessions() {
        sessionMap.entrySet().removeIf(entry -> {
            Session session = entry.getValue().getSession();
            return !session.isOpen();
        });
    }

    public void addChatHistory(String sessionId, Map<String, String> message) {
        List<Map<String, String>> chatHistory = sessionMap.get(sessionId).getChatHistory();
        if (chatHistory.size() >= MAX_CHAT_HISTORY) {
            chatHistory.remove(0);
        }
        chatHistory.add(message);
    }

    @PreDestroy
    public void destroy() {
        sessionMap.forEach((sessionId, wsDTO) -> {
            try {
                wsDTO.getSession().close();
            } catch (IOException ignore) {}
        });
        sessionMap.clear();
    }

}
