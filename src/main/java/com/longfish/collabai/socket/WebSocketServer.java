package com.longfish.collabai.socket;

import com.longfish.collabai.enums.StatusCodeEnum;
import com.longfish.collabai.properties.PyProperties;
import com.longfish.collabai.service.RedisService;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.longfish.collabai.constant.DatabaseConstant.AI_SESSION;

@Component
@ServerEndpoint(value = "/ws/msg/{sessionId}")
@Slf4j
public class WebSocketServer {

    private static final Map<String, Session> sessionMap = new HashMap<>();

    private static RedisService redisService;

    private static PyProperties pyProperties;

    @Autowired
    public void setChatService(RedisService redisService) {
        WebSocketServer.redisService = redisService;
    }

    @Autowired
    public void setPyProperties(PyProperties pyProperties) {
        WebSocketServer.pyProperties = pyProperties;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("sessionId") String sessionId) throws IOException {
        Set<String> keys = redisService.keysPrefix(AI_SESSION);
        List<String> list = new ArrayList<>();
        keys.forEach(k -> {
            String[] split = k.split(":");
            list.add(split[split.length - 1]);
        });
        if (!list.contains(sessionId)) {
            session.getBasicRemote().sendText("未知的 sessionId");
            session.close();
            return;
        }
        if (sessionMap.containsKey(sessionId)) {
            session.getBasicRemote().sendText(sessionId + " 已在线！");
            session.close();
            return;
        }

        log.info("AI会话 {} 建立连接", sessionId);
        sessionMap.put(sessionId, session);
    }

    @OnError
    public void onError(Throwable e) {
        log.error(StatusCodeEnum.WEBSOCKET_ERROR.getDesc() + "{}", e.getMessage());
        e.printStackTrace();
    }

    @OnMessage
    public void onMessage(String message, @PathParam("sessionId") String sessionId) {
        List<String> user = new ArrayList<>();
        List<String> assist = new ArrayList<>();

        String key = new ArrayList<>(redisService.keys("*" + sessionId + "*")).get(0);
        for (int i = 1; i < redisService.lSize(key) / 2; i++) {
            user.add((String) redisService.lIndex(key, i));
            assist.add((String) redisService.lIndex(key, i + 1));
        }
        user.add(message);
        InputStream is = req(user, assist);
        if (is != null) {
            redisService.lPush(key, message);
            redisService.lPush(key, render(is, sessionMap.get(sessionId)));
        }
    }

    @OnClose
    public void onClose(@PathParam("sessionId") String sessionId) {
        sessionMap.remove(sessionId);
    }

    public static String render(InputStream is, Session session) {
        try {
            byte[] buffer = new byte[1024];
            int bytesRead;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            OutputStream os = session.getBasicRemote().getSendStream();
            while ((bytesRead = is.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
                os.write(buffer, 0, bytesRead);
                os.flush();
            }
            os.flush();
            os.close();
            return baos.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static InputStream req(List<String> user, List<String> assist) {
        try {
            String baseUrl = pyProperties.getBaseUrl() + "/chat?";
            baseUrl += build("user", user);
            baseUrl += build("assistant", assist);
            while (baseUrl.charAt(baseUrl.length() - 1) == '&')
                baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            URL url = new URL(baseUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "text/event-stream");
            return connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String build(String paramName, List<String> list) {
        StringBuilder result = new StringBuilder();
        for (String value : list) {
            result.append(paramName).append("=").append(URLEncoder.encode(value, StandardCharsets.UTF_8)).append("&");
        }
        return result.toString();
    }

}
